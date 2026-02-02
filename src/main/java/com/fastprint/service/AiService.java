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

            // Hitung per status
            Map<String, Long> countByStatus = allProduk.stream()
                    .collect(Collectors.groupingBy(p -> p.getStatus().getNamaStatus(), Collectors.counting()));

            // Hitung per kategori
            Map<String, Long> countByKategori = allProduk.stream()
                    .collect(Collectors.groupingBy(p -> p.getKategori().getNamaKategori(), Collectors.counting()));

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

            // --- AUDIT TRAIL INFO ---
            Produk terbaru = allProduk.stream()
                    .filter(p -> p.getCreatedAt() != null)
                    .max(Comparator.comparing(Produk::getCreatedAt))
                    .orElse(null);

            Produk terlama = allProduk.stream()
                    .filter(p -> p.getCreatedAt() != null)
                    .min(Comparator.comparing(Produk::getCreatedAt))
                    .orElse(null);

            String auditInfo = "";
            if (terbaru != null)
                auditInfo += "- Produk Terbaru: " + terbaru.getNamaProduk() + " (" + terbaru.getCreatedAt() + ")\n";
            if (terlama != null)
                auditInfo += "- Produk Terlama: " + terlama.getNamaProduk() + " (" + terlama.getCreatedAt() + ")\n";

            String dataSummary = String.format(
                    "REAL-TIME DATA SNAPSHOT:\n" +
                            "- Total Produk: %d\n" +
                            "- Statistik Status: %s\n" +
                            "- Statistik Kategori: %s\n" +
                            "- Kategori Terkecil: %s\n" +
                            "- Kategori Terbanyak: %s\n" +
                            "%s",
                    total, countByStatus.toString(), countByKategori.toString(),
                    String.join(", ", katTerkecil),
                    String.join(", ", katTerbanyak),
                    auditInfo);

            String systemContext = "Anda adalah 'FastPrint AI Assistant'.\n" +
                    "INFORMASI VALIDASI FORM PRODUK (Sangat Penting!):\n" +
                    "1. Nama Produk: Wajib diisi (tidak boleh kosong/spasi saja).\n" +
                    "2. Harga: Wajib diisi, harus berupa angka, dan HARUS POSITIF (tidak boleh nol atau negatif).\n" +
                    "3. Kategori: Wajib dipilih dari daftar yang tersedia.\n" +
                    "4. Status: Wajib dipilih dari daftar yang tersedia.\n" +
                    "Catatan: TIDAK ADA validasi tanggal manual atau pengecekan nama duplikat saat ini.\n\n" +
                    "TUGAS: Jawab pertanyaan user berdasarkan data snapshot dan aturan validasi di atas. Gunakan Bahasa Indonesia yang ramah.";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.1-8b-instant");
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", systemContext + "\n\n" + dataSummary),
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
            return "Maaf, sedang ada gangguan koneksi ke otak AI saya.";
        } catch (Exception e) {
            return "Ada kendala teknis singkat. Silahkan coba lagi ya!";
        }
    }
}
