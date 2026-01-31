package com.fastprint.service;

import com.fastprint.dto.ProdukDto;
import com.fastprint.entity.Kategori;
import com.fastprint.entity.Produk;
import com.fastprint.entity.Status;
import com.fastprint.repository.KategoriRepository;
import com.fastprint.repository.ProdukRepository;
import com.fastprint.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ProdukService {

    @Autowired
    private ProdukRepository produkRepository;

    @Autowired
    private KategoriRepository kategoriRepository;

    @Autowired
    private StatusRepository statusRepository;

    // We can use this to fetch data if needed
    private final WebClient webClient;

    public ProdukService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://recruitment.fastprint.co.id/tes/api_tes_programmer").build();
    }

    public List<Produk> findAllProduk() {
        return produkRepository.findAll();
    }

    public List<Produk> findProdukBisaDijual() {
        return produkRepository.findByStatusBisaDijual();
    }

    public List<Produk> findByStatus(String statusName) {
        return produkRepository.findByStatus_NamaStatus(statusName);
    }

    public Produk getProdukById(Long id) {
        return produkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produk not found with id: " + id));
    }

    public void saveProduk(Produk produk) {
        // Ensure Kategori exists or save it
        if (produk.getKategori() != null && produk.getKategori().getIdKategori() == null) {
            Optional<Kategori> katOpt = kategoriRepository.findByNamaKategori(produk.getKategori().getNamaKategori());
            if (katOpt.isPresent()) {
                produk.setKategori(katOpt.get());
            } else {
                kategoriRepository.save(produk.getKategori());
            }
        }

        // Ensure Status exists or save it
        if (produk.getStatus() != null && produk.getStatus().getIdStatus() == null) {
            Optional<Status> statOpt = statusRepository.findByNamaStatus(produk.getStatus().getNamaStatus());
            if (statOpt.isPresent()) {
                produk.setStatus(statOpt.get());
            } else {
                statusRepository.save(produk.getStatus());
            }
        }

        produkRepository.save(produk);
    }

    public void deleteProduk(Long id) {
        produkRepository.deleteById(id);
    }

    // Logic to sync from API
    // Note: This matches the typical response format of the recruitment test
    // Logic to sync from API
    public void syncDataFromApi() {
        try {
            // Calculate Username: tesprogrammer + ddMMyy + C + HH
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            String datePart = now.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyy"));
            String hourPart = now.format(java.time.format.DateTimeFormatter.ofPattern("HH"));
            String username = "tesprogrammer" + datePart + "C" + hourPart;

            // Calculate Password: MD5(bisacoding-dd-MM-yy)
            String passwordDatePart = now.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yy"));
            String passwordRaw = "bisacoding-" + passwordDatePart;
            String password = getMd5(passwordRaw);

            System.out.println("Syncing with Username: " + username);
            // System.out.println("Password Raw: " + passwordRaw);
            // System.out.println("Password Hash: " + password);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("username", username);
            formData.add("password", password);

            // This structure depends on the specific API response
            Map response = webClient.post()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("data")) {
                List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");

                for (Map<String, Object> item : dataList) {
                    String nama = (String) item.get("nama_produk");
                    String kategoriName = (String) item.get("kategori");
                    String statusName = (String) item.get("status");
                    String hargaStr = (String) item.get("harga");

                    // Parse harga
                    Double harga = 0.0;
                    try {
                        harga = Double.parseDouble(hargaStr);
                    } catch (Exception e) {
                    }

                    // Find or Create Kategori
                    Kategori kategori = kategoriRepository.findByNamaKategori(kategoriName)
                            .orElseGet(() -> {
                                Kategori k = new Kategori();
                                k.setNamaKategori(kategoriName);
                                return kategoriRepository.save(k);
                            });

                    // Find or Create Status
                    Status status = statusRepository.findByNamaStatus(statusName)
                            .orElseGet(() -> {
                                Status s = new Status();
                                s.setNamaStatus(statusName);
                                return statusRepository.save(s);
                            });

                    // Save Produk
                    Produk produk = new Produk();
                    produk.setNamaProduk(nama);
                    produk.setHarga(harga);
                    produk.setKategori(kategori);
                    produk.setStatus(status);

                    produkRepository.save(produk);
                }
            } else {
                System.out.println("API Response does not contain 'data': " + response);
            }

        } catch (Exception e) {
            System.err.println("Failed to sync from API: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getMd5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            java.math.BigInteger no = new java.math.BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Kategori> findAllKategori() {
        return kategoriRepository.findAll();
    }

    public List<Status> findAllStatus() {
        return statusRepository.findAll();
    }
}
