package org.example;

import org.apache.poi.util.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class BankApplication{

    public static void main(String[] args) {
        IOUtils.setByteArrayMaxOverride(1024 * 1024 * 1024);
        SpringApplication.run(BankApplication.class, args);
    }
}