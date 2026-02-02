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
import java.text.NumberFormat;
import java.util.Locale;

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

    private String formatRupiah(Double h) {
        if (h == null)
            return "Rp 0";
        NumberFormat formatter = NumberFormat.getInstance(new Locale("id", "ID"));
        return "Rp " + formatter.format(h.longValue());
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

            // Analisa Harga
            Produk termahal = allProduk.stream().max(Comparator.comparing(Produk::getHarga)).orElse(null);
            Produk termurah = allProduk.stream().min(Comparator.comparing(Produk::getHarga)).orElse(null);

            // Analisa Statistik Kategori
            long minCount = countByKategori.values().stream().min(Long::compare).orElse(0L);
            long maxCount = countByKategori.values().stream().max(Long::compare).orElse(0L);

            List<String> katTerkecil = countByKategori.entrySet().stream()
                    .filter(e -> e.getValue() == minCount)
                    .map(e -> e.getKey() + " (" + e.getValue() + " produk)")
                    .collect(Collectors.toList());

            List<String> katTerbanyak = countByKategori.entrySet().stream()
                    .filter(e -> e.getValue() == maxCount)
                    .map(e -> e.getKey() + " (" + e.getValue() + " produk)")
                    .collect(Collectors.toList());

            // --- AUDIT TRAIL ---
            Produk terbaru = allProduk.stream()
                    .filter(p -> p.getCreatedAt() != null)
                    .max(Comparator.comparing(Produk::getCreatedAt))
                    .orElse(null);

            String dataSummary = String.format(
                    "STATISTIK REAL-TIME:\n" +
                            "- Total Keseluruhan: %d produk\n" +
                            "- Status: %s\n" +
                            "- Kategori Terbanyak: %s\n" +
                            "- Kategori Terkecil: %s\n" +
                            "- Termahal: %s (%s)\n" +
                            "- Termurah: %s (%s)\n" +
                            "- Barang Baru: %s\n",
                    total, countByStatus.toString(),
                    String.join(", ", katTerbanyak),
                    String.join(", ", katTerkecil),
                    termahal != null ? termahal.getNamaProduk() : "N/A",
                    formatRupiah(termahal != null ? termahal.getHarga() : 0.0),
                    termurah != null ? termurah.getNamaProduk() : "N/A",
                    formatRupiah(termurah != null ? termurah.getHarga() : 0.0),
                    terbaru != null ? terbaru.getNamaProduk() : "Belum ada");

            String systemContext = "Anda adalah 'FastPrint AI Assistant', asisten cerdas yang dikembangkan oleh Jefri pada 2 Februari 2026 untuk membantu sistem operasional Fast Print.\n"
                    +
                    "INFORMASI OPERASIONAL (Gunakan ini!):\n" +
                    "1. EDIT Produk: Klik ikon PENSIL BIRU ( \uD83D\uDCDD bi-pencil-fill ) pada kolom Action di tabel. Ubah data di popup, lalu klik 'Simpan'.\n"
                    +
                    "2. HAPUS Produk: Klik ikon TEMPAT SAMPAH MERAH ( \uD83D\uDDD1️ bi-trash3-fill ) pada kolom Action. Konfirmasi hapus di popup.\n"
                    +
                    "3. TAMBAH Produk: Klik tombol '+ Tambah Produk' (biru) di pojok kanan atas tabel.\n" +
                    "4. SYNC API: Klik tombol 'Sync API' (putih/ikon panah memutar) di sebelah tombol Tambah Produk untuk menyinkronkan data otomatis dari server pusat. Tidak ada pilihan menu lain.\n\n"
                    +
                    "ATURAN JAWABAN:\n" +
                    "- HAPUS semua 'alur' panjang yang tidak relevan (seperti verifikasi alamat, lokasi, material, dll). Fokus HANYA pada ikon tombol di atas.\n"
                    +
                    "- Gunakan format **Bold** untuk hal penting.\n" +
                    "- Tampilkan ikon menggunakan emoji yang sesuai ( \uD83D\uDCDD untuk edit, \uD83D\uDDD1️ untuk hapus).\n"
                    +
                    "- Selalu gunakan Bahasa Indonesia yang ramah.\n" +
                    "- ACTION TAGS (Gunakan HANYA jika user MEMINTA EKSPLISIT untuk melakukan aksi ini, jangan gunakan jika hanya bertanya info):\n"
                    +
                    "  * Jika user ingin TAMBAH produk: Tulis '[[ACTION:CREATE]]' di akhir.\n" +
                    "  * Jika user ingin EDIT produk ID X: Tulis '[[ACTION:EDIT:X]]' di akhir.\n" +
                    "  * Jika user ingin HAPUS produk ID X: Tulis '[[ACTION:DELETE:X]]' di akhir.\n" +
                    "  * Jika user ingin SYNC API: Tulis '[[ACTION:SYNC_API]]' di akhir.\n\n" +
                    "DATA SNAPSHOT:\n" +
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
            return "Maaf, sistem sedang sibuk. Mohon coba sesaat lagi.";
        } catch (Exception e) {
            return "Terjadi kendala teknis singkat. Silahkan coba lagi ya!";
        }
    }
}
