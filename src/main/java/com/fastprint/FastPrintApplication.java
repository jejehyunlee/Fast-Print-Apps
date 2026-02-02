package com.fastprint;

import com.fastprint.service.ProdukService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class FastPrintApplication {

    public static void main(String[] args) {
        // Load .env variables into System Properties for local development
        try {
            Dotenv logger = Dotenv.configure().ignoreIfMissing().load();
            logger.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        } catch (Exception e) {
            // Silently ignore if .env is missing (production environment)
        }
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
