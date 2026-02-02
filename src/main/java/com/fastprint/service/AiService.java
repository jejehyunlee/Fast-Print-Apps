package com.fastprint.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Service
public class AiService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    private final WebClient webClient;

    @Autowired
    private ProdukService produkService;

    public AiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateResponse(String prompt) {
        try {
            // Pembersihan Key (Penting!)
            String finalApiKey = System.getenv("GROQ_API_KEY");
            if (finalApiKey == null || finalApiKey.isEmpty()) {
                finalApiKey = System.getProperty("GROQ_API_KEY");
            }
            if (finalApiKey == null || finalApiKey.isEmpty()) {
                finalApiKey = apiKey;
            }

            if (finalApiKey != null) {
                finalApiKey = finalApiKey.trim(); // Hapus spasi liar
            }

            if (finalApiKey == null || finalApiKey.isEmpty()) {
                return "Waduh, API Key Groq belum terdeteksi. Silahkan cek konfigurasi .env Anda.";
            }

            // Informasi Nyata dari Database
            int totalProduk = produkService.findAllProduk().size();
            int bisaDijual = produkService.findProdukBisaDijual().size();

            String systemContext = "Anda adalah 'FastPrint AI', asisten cerdas khusus untuk FastPrint System.\n" +
                    "Konteks Sistem:\n" +
                    "- Dashboard: Statistik ringkas.\n" +
                    "- Master Produk: Kelola data (Tambah/Edit/Hapus).\n" +
                    "- Data Nyata Saat Ini: " + totalProduk + " total produk (" + bisaDijual + " bisa dijual).\n\n" +
                    "Gunakan Bahasa Indonesia yang ramah. Jawab pertanyaan user sesuai konteks aplikasi FastPrint.";

            Map<String, Object> requestBody = new HashMap<>();
            // Menggunakan llama-3.1-8b-instant yang paling stabil dan cepat
            requestBody.put("model", "llama-3.1-8b-instant");
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", systemContext),
                    Map.of("role", "user", "content", prompt)));

            Map response = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + finalApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("choices")) {
                List choices = (List) response.get("choices");
                if (!choices.isEmpty()) {
                    Map firstChoice = (Map) choices.get(0);
                    Map message = (Map) firstChoice.get("message");
                    return (String) message.get("content");
                }
            }
            return "Maaf, Groq AI sedang tidak memberikan respon.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Terjadi kendala teknis: " + e.getMessage();
        }
    }
}
