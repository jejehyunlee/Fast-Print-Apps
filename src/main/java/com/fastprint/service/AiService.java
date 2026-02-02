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
            // Mengambil sedikit data nyata untuk konteks AI
            int totalProduk = produkService.findAllProduk().size();
            int bisaDijual = produkService.findProdukBisaDijual().size();

            String systemContext = "Anda adalah 'FastPrint AI', asisten pintar khusus untuk aplikasi FastPrint (Sistem Manajemen Produk Percetakan).\n"
                    +
                    "Konteks Aplikasi:\n" +
                    "- Nama: FastPrint\n" +
                    "- Fitur Utama: Dashboard (Statistik), Master Produk (Data Produk, Kategori, Status), Penjualan.\n"
                    +
                    "- Operasi: Pengguna bisa menambah, mengedit, dan menghapus produk di menu 'Master Produk'.\n" +
                    "- Statistik Saat Ini: Terdapat total " + totalProduk + " produk di sistem, di mana " + bisaDijual
                    + " statusnya 'bisa dijual'.\n\n" +
                    "Tugas Anda:\n" +
                    "1. Jawablah pertanyaan pengguna terkait penggunaan aplikasi FastPrint.\n" +
                    "2. Gunakan Bahasa Indonesia yang ramah, profesional, dan sedikit santai (seperti rekan kerja).\n" +
                    "3. Jika ditanya cara tambah data, beri tahu untuk ke 'Master Produk' -> tombol 'Tambah Produk'.\n"
                    +
                    "4. Jika ditanya soal harga, ingatkan bahwa input harga harus berupa angka.\n" +
                    "5. Jangan menjawab pertanyaan di luar konteks aplikasi kecuali jika sangat umum, namun arahkan kembali ke FastPrint.";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.3-70b-versatile");
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", systemContext),
                    Map.of("role", "user", "content", prompt)));

            Map response = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
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
            return "Maaf, FastPrint AI sedang sulit dihubungi. Coba lagi nanti ya!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Waduh, ada kendala teknis saat saya mencoba berpikir. Error: " + e.getMessage();
        }
    }
}
