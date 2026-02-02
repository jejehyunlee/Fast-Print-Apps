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

            // Mencari produk terbaru dan terlama (Audit Trail)
            Produk terbaru = allProduk.stream()
                    .filter(p -> p.getCreatedAt() != null)
                    .max((p1, p2) -> p1.getCreatedAt().compareTo(p2.getCreatedAt()))
                    .orElse(null);

            Produk terlama = allProduk.stream()
                    .filter(p -> p.getCreatedAt() != null)
                    .min((p1, p2) -> p1.getCreatedAt().compareTo(p2.getCreatedAt()))
                    .orElse(null);

            String auditInfo = "";
            if (terbaru != null)
                auditInfo += "- Produk Terbaru: " + terbaru.getNamaProduk() + " (" + terbaru.getCreatedAt() + ")\n";
            if (terlama != null)
                auditInfo += "- Produk Terlama: " + terlama.getNamaProduk() + " (" + terlama.getCreatedAt() + ")\n";

            String dataSummary = String.format(
                    "Ringkasan Data FastPrint Saat Ini:\n" +
                            "- Total Produk: %d\n" +
                            "- Status: %s\n" +
                            "- Kategori: %s\n" +
                            "%s",
                    total, countByStatus.toString(), countByKategori.toString(), auditInfo);

            String systemContext = "Anda adalah 'FastPrint AI Assistant'. Anda adalah pakar dalam mengelola data percetakan di aplikasi ini.\n"
                    +
                    "IDENTITAS DAN GAYA:\n" +
                    "- Gunakan Bahasa Indonesia yang sangat ramah, profesional, dan to-the-point.\n" +
                    "- Selalu berikan data yang akurat berdasarkan ringkasan data yang diberikan sistem.\n\n" +
                    "PENGETAHUAN APLIKASI:\n" +
                    "- Menu Dashboard: Menampilkan grafik dan ringkasan.\n" +
                    "- Menu Master Produk: Tempat menambah (tombol biru), edit (ikon pensil), atau hapus (ikon sampah) produk.\n"
                    +
                    "- Validasi: Harga harus angka, nama tidak boleh kosong.\n\n" +
                    dataSummary + "\n" +
                    "INSTRUKSI: Jika user bertanya jumlah, jawab dengan angka persis dari data di atas. Jangan mengarang data.";

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
            return "FastPrint AI sedang memproses permintaan lain. Coba lagi dalam beberapa saat.";
        } catch (Exception e) {
            return "Ada kendala teknis singkat. Silahkan coba lagi ya!";
        }
    }
}
