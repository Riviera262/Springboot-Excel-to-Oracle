package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController()
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/uploadExcel")
    public String uploadExcel(@RequestParam("file")MultipartFile file){
        if (file.isEmpty()){
            return "Please choose a excel file to upload";
        }

        try {
            //Get all the data from the excel file
            transactionService.ImportExcel(file.getInputStream());
            return "Upload and process data complete!";
        } catch (Exception e) {
            log.info("Error uploading excel file: " + file.getOriginalFilename(), e);
            return "Error processing file: " + e.getMessage();
        }
    }
    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllTransactions(){
        transactionService.deleteAllTransactions();
        return ResponseEntity.ok("Delete all transactions data in Oracle complete!");
    }
}
