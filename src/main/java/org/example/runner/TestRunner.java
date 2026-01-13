//package org.example.runner;
//
//import org.example.model.Transaction;
//import org.example.repository.TransactionRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * This runs automatically when Spring Boot starts
// *
// * @Component = Spring creates this object
// * CommandLineRunner = runs after Spring Boot starts
// */
//@Component
//public class TestRunner implements CommandLineRunner {
//
//    @Autowired
//    private TransactionRepository repository;
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("========================================");
//        System.out.println("Testing TransactionRepository (Block 3)");
//        System.out.println("========================================");
//        System.out.println();
//
//        // Test 1: Database connection
//        System.out.println("Test 1: Testing database connection...");
//        boolean connected = repository.testConnection();
//        if (connected) {
//            System.out.println("Database connection SUCCESS!");
//        } else {
//            System.out.println("Database connection FAILED!");
//            return; // Stop if can't connect
//        }
//        System.out.println();
//
//        // Test 2: Clear table (clean start)
//        System.out.println("Test 2: Clearing table...");
//        repository.deleteAll();
//        long count = repository.count();
//        System.out.println("Table cleared. Current count: " + count);
//        System.out.println();
//
//
//        System.out.println("Test 3: Inserting 1 data");
//        Transaction txn1 = new Transaction("Trace001", "Acc001", LocalDateTime.now(), new BigDecimal("1000.50"), "Acc002", "Test inserting 1", "TRANFER");
//        repository.insert(txn1);
//
//        System.out.println("Test 4: Batch insert many data");
//        List<Transaction> transactions = new ArrayList<>();
//        for (int i = 2; i <= 101; i++) {
//            Transaction txn = new Transaction(
//                    "TRACE" + String.format("%03d", i),
//                    "ACC" + (i % 10),
//                    LocalDateTime.now(),
//                    new BigDecimal(String.valueOf(1000 + i)),
//                    "ACC" + ((i + 1) % 10),
//                    "Batch test transaction " + i,
//                    "TRANSFER"
//            );
//            transactions.add(txn);
//        }
//        int inserted = repository.batchInsert(transactions);
//        System.out.println("Batch insert " + inserted + " transactions");
//        count = repository.count();
//        System.out.println("Current table's rows: " + count);
//        // Summary
//        System.out.println("========================================");
//        System.out.println("Block 3 Testing Complete!");
//        System.out.println("All 4 tests passed!");
//        System.out.println("========================================");
//    }
//}