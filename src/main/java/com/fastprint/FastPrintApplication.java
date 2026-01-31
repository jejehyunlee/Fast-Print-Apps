package com.fastprint;

import com.fastprint.service.ProdukService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FastPrintApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastPrintApplication.class, args);
    }

    @Bean
    CommandLineRunner run(ProdukService produkService) {
        return args -> {
            // Check if DB is empty, if so, sync from API
            if (produkService.findAllProduk().isEmpty()) {
                System.out.println("Database empty, syncing from API...");
                produkService.syncDataFromApi();
                System.out.println("Sync complete.");
            }
        };
    }
}
