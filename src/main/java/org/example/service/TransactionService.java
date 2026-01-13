package org.example.service;

import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.example.model.Transaction;
import org.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Slf4j
@Service
public class TransactionService{

    @Autowired
    private TransactionRepository repository;

    private static final int BATCH_INSERT_LIMIT = 5000;

    //Make sure whatever excel format is, just need to be similar to LocalDateTime format
    private static final List<DateTimeFormatter> ACCEPTED_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("d/M/yy H:mm"),
            DateTimeFormatter.ofPattern("d/M/yyyy H:mm"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a", new Locale("vi", "VN")), //process SA/CH
            DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a", Locale.ENGLISH),        //process AM/PM
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    );

    private LocalDateTime tryParseDateTime(String dateStr) {
        for (DateTimeFormatter formatter : ACCEPTED_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                //Try the next format if this one doesn't match
                continue;
            }
        }
        //Null if it not match any format
        return null;
    }

    public void ImportExcel(InputStream inputStream) throws IOException {
        long startTime = System.currentTimeMillis();


        try (//Open excel file and read it
            Workbook workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            int success_row = 0;
            int failed_row = 0;
            int duplicate_row = 0;
            int db_duplicate_row = 0;
            int total_valid_row = 0;

            List<Transaction> validTransactionList = new ArrayList<>(BATCH_INSERT_LIMIT);

            //Check if trace data duplicate
            Set<String> seenTrace = new HashSet<>();

            for (Row row : sheet) {
                //Skip header of the excel file
                if (row.getRowNum() == 0) {
                    continue;
                }

                String trace = getCellValue(row.getCell(0));
                String tranxTimeStr = getCellValue(row.getCell(2));
                String amountStr = getCellValue(row.getCell(3));
                //log.info("Tranx_Time: "+ tranxTimeStr);
                //log.info("Amount money: "+ amountStr);

                if (seenTrace.contains(trace)) {
                    log.info("Row "+ (row.getRowNum() + 1) + " skipped due to duplicate trace: " + trace);
                    duplicate_row++;
                    continue;
                }

                String errorMsg = validateData(tranxTimeStr, amountStr);

                //If data are valid => make them become an object and put it into valid transactions list
                if (errorMsg.isEmpty()) {
                    Transaction txn = new Transaction();
                    txn.setTrace(trace);
                    txn.setFromAcc(getCellValue(row.getCell(1)));
                    txn.setTranxTime(tryParseDateTime(tranxTimeStr));
                    txn.setAmount(new BigDecimal(amountStr));
                    txn.setToAcc(getCellValue(row.getCell(4)));
                    txn.setRemark(getCellValue(row.getCell(5)));
                    txn.setTranxType(getCellValue(row.getCell(6)));

                    validTransactionList.add(txn);
                    seenTrace.add(trace);
                    total_valid_row++;

                } else {
                    log.info("Row " + (row.getRowNum() + 1) + " error: " + errorMsg);
                    failed_row++;
                }

                //If list size reach limit of batch insert
                if (validTransactionList.size() == BATCH_INSERT_LIMIT) {
                    int inserted = repository.batchInsert(validTransactionList);
                    success_row += inserted;
                    db_duplicate_row += (validTransactionList.size() - inserted);
                    validTransactionList.clear();
                }
            }

            if (!validTransactionList.isEmpty()) {
                int inserted = repository.batchInsert(validTransactionList);
                success_row += inserted;
                db_duplicate_row += (validTransactionList.size() - inserted);
            }

            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1000.0;

            log.info("Time duration to complete the import excel: " + duration +" s");
            log.info("Total valid rows: " + total_valid_row);
            log.info("Duplicate rows in excel: " + duplicate_row);
            log.info("Already exist in Oracle (skipped): " + db_duplicate_row);
            log.info("Success rows (insertes row): " + success_row);
            log.info("Failed rows (wrong format): " + failed_row);
        }
    }

    //Check tranxTime and amount format
    private String validateData(String tranxTimeStr, String amountStr){
        StringBuilder errors = new StringBuilder();
        validateLocalDateTime(tranxTimeStr, errors);
        validationBigDecimal(amountStr, errors);
        return errors.toString();
    }

    //Check DateTime format
    private void validateLocalDateTime(String tranxTimeStr, StringBuilder error){
        if (tryParseDateTime(tranxTimeStr) == null) {
            error.append("LocalDateTime format required: yyyy-MM-dd HH:mm:ss or something similar); ");
        }
    }

    //Check money format
    private void validationBigDecimal(String amountStr, StringBuilder error){
       if (!amountStr.matches("^[0-9]+(\\.[0-9]{1,2})?$")){
           error.append("Invalid money format");
       }
    }

    //Get data from excel cell
    private String getCellValue(Cell cell){
        if (cell == null){
            return "";
        }

        CellType cellType = cell.getCellType();

        if (cellType == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cellType == CellType.NUMERIC) {
            double numericValue = cell.getNumericCellValue();
            return BigDecimal.valueOf(numericValue).toPlainString();
        } else if (cellType == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cellType == CellType.BLANK) {
            return "";
        } else {
            // Date cells và các type khác sẽ dùng toString()
            return cell.toString().trim();
        }
    }

    @Transactional
    public void deleteAllTransactions(){
        repository.deleteAll();
    }
}