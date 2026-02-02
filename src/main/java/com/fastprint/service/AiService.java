package com.fastprint.service;

import com.fastprint.entity.Kategori;
import com.fastprint.entity.Produk;
import com.fastprint.entity.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class AiService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    private final WebClient webClient;

    @Autowired
    private ProdukService produkService;

    public AiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateResponse(String prompt) {
        try {
            // Resolusi API Key dengan trim dan fallback
            String finalApiKey = System.getenv("GROQ_API_KEY");
            if (finalApiKey == null || finalApiKey.isEmpty()) {
                finalApiKey = System.getProperty("GROQ_API_KEY");
            }
            if (finalApiKey == null || finalApiKey.isEmpty()) {
                finalApiKey = apiKey;
            }
            if (finalApiKey != null)
                finalApiKey = finalApiKey.trim();

            if (finalApiKey == null || finalApiKey.isEmpty()) {
                return "Maaf, konfigurasi API Key belum lengkap di server.";
            }

            // --- DATA SNAPSHOT UNTUK AI ---
            List<Produk> allProduk = produkService.findAllProduk();
            int total = allProduk.size();

            // Statistik Status & Kategori
            Map<String, Long> countByStatus = allProduk.stream()
                    .collect(Collectors.groupingBy(p -> p.getStatus().getNamaStatus(), Collectors.counting()));
            Map<String, Long> countByKategori = allProduk.stream()
                    .collect(Collectors.groupingBy(p -> p.getKategori().getNamaKategori(), Collectors.counting()));

            // Analisa Harga (Terkecil/Terbesar)
            Produk termahal = allProduk.stream()
                    .max(Comparator.comparing(Produk::getHarga))
                    .orElse(null);
            Produk termurah = allProduk.stream()
                    .min(Comparator.comparing(Produk::getHarga))
                    .orElse(null);

            // Analisa Statistik Kategori (Terkecil/Terbanyak)
            long minCount = countByKategori.values().stream().min(Long::compare).orElse(0L);
            long maxCount = countByKategori.values().stream().max(Long::compare).orElse(0L);

            List<String> katTerkecil = countByKategori.entrySet().stream()
                    .filter(e -> e.getValue() == minCount)
                    .map(e -> e.getKey() + " (" + e.getValue() + ")")
                    .collect(Collectors.toList());

            List<String> katTerbanyak = countByKategori.entrySet().stream()
                    .filter(e -> e.getValue() == maxCount)
                    .map(e -> e.getKey() + " (" + e.getValue() + ")")
                    .collect(Collectors.toList());

            // --- AUDIT TRAIL ---
            Produk terbaru = allProduk.stream()
                    .filter(p -> p.getCreatedAt() != null)
                    .max(Comparator.comparing(Produk::getCreatedAt))
                    .orElse(null);
            Produk terlama = allProduk.stream()
                    .filter(p -> p.getCreatedAt() != null)
                    .min(Comparator.comparing(Produk::getCreatedAt))
                    .orElse(null);

            String auditPriceInfo = "";
            if (termahal != null)
                auditPriceInfo += "- Produk Termahal: " + termahal.getNamaProduk() + " (Rp " + termahal.getHarga()
                        + ")\n";
            if (termurah != null)
                auditPriceInfo += "- Produk Termurah: " + termurah.getNamaProduk() + " (Rp " + termurah.getHarga()
                        + ")\n";
            if (terbaru != null)
                auditPriceInfo += "- Produk Terbaru Masuk: " + terbaru.getNamaProduk() + " (" + terbaru.getCreatedAt()
                        + ")\n";
            if (terlama != null)
                auditPriceInfo += "- Produk Terlama Sedekat: " + terlama.getNamaProduk() + " (" + terlama.getCreatedAt()
                        + ")\n";

            String dataSummary = String.format(
                    "REAL-TIME DATA SNAPSHOT:\n" +
                            "- Total Produk: %d\n" +
                            "- Statistik Status: %s\n" +
                            "- Statistik Kategori: %s\n" +
                            "- Kategori Terkecil (Jumlah Sama): %s\n" +
                            "- Kategori Terbanyak: %s\n" +
                            "%s",
                    total, countByStatus.toString(), countByKategori.toString(),
                    String.join(", ", katTerkecil),
                    String.join(", ", katTerbanyak),
                    auditPriceInfo);

            String systemContext = "Anda adalah 'FastPrint AI Assistant'. Anda memiliki akses penuh ke statistik data produk.\n"
                    +
                    "TUGAS: Jawablah pertanyaan pengguna dengan data angka yang sangat akurat dari snapshot di bawah.\n"
                    +
                    "INFORMASI HARGA: Jika ditanya harga termahal atau termurah, gunakan data spesifik yang disediakan.\n"
                    +
                    "Gaya Bahasa: Ramah, Profesional, Informatif.\n\n" +
                    dataSummary;

            Map<String, Object> requestBody = new HashMap<>();
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
            return "Maaf, saya sedang kehilangan fokus. Silahkan tanya lagi.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ada kendala teknis singkat. Silahkan coba lagi ya!";
        }
    }
}
