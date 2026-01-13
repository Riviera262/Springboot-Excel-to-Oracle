package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import java.io.FileOutputStream;
import java.util.Random;

public class DataGenerator {

    public static void main(String[] args) {
        // Sử dụng SXSSFWorkbook để tối ưu bộ nhớ cho file lớn
        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        Sheet sheet = workbook.createSheet("Transactions");
        Random random = new Random();

        // 1. Tạo tiêu đề (Header)
        Row header = sheet.createRow(0);
        String[] columns = {"TRACE", "FROM_ACC", "TRANX_TIME", "AMOUNT", "TO_ACC", "REMARK", "TRANX_TYPE"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // 2. Tạo 50,000 dòng dữ liệu mẫu
        System.out.println("Đang bắt đầu tạo dữ liệu...");
        for (int i = 1; i <= 50000; i++) {
            Row row = sheet.createRow(i);

            row.createCell(0).setCellValue("TRX" + String.format("%06d", i)); // Ví dụ: TRX000001
            row.createCell(1).setCellValue("111222333" + (random.nextInt(90) + 10)); // Số TK giả

            // Sử dụng đúng định dạng mà bạn đã cấu hình trong Service để test cho chuẩn
            row.createCell(2).setCellValue("2026-01-12 10:00:00");

            // Tạo số tiền ngẫu nhiên từ 1,000.00 đến 1,000,000.00
            double amount = 1000 + (1000000 - 1000) * random.nextDouble();
            row.createCell(3).setCellValue(String.format("%.2f", amount));

            row.createCell(4).setCellValue("999888777" + (random.nextInt(90) + 10));
            row.createCell(5).setCellValue("Chuyen khoan thanh toan hoa don " + i);
            row.createCell(6).setCellValue("FT");

            if (i % 10000 == 0) {
                System.out.println("Đã tạo xong " + i + " dòng...");
            }
        }

        // 3. Xuất file
        try (FileOutputStream fileOut = new FileOutputStream("MB_Test_50k.xlsx")) {
            workbook.write(fileOut);
            workbook.dispose(); // Xóa file tạm của SXSSF
            System.out.println("Tạo file thành công! Tên file: MB_Test_50k.xlsx");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}