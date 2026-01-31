package com.fastprint.controller;

import com.fastprint.entity.Produk;
import com.fastprint.service.ProdukService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private ProdukService produkService;

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Produk> allProducts = produkService.findAllProduk();

        long totalProduk = allProducts.size();
        long bisaDijualCount = allProducts.stream()
                .filter(p -> p.getStatus() != null && "bisa dijual".equalsIgnoreCase(p.getStatus().getNamaStatus()))
                .count();
        long tidakBisaDijualCount = totalProduk - bisaDijualCount;
        
        // Group by Kategori for Chart
        List<String> kategoriLabels = allProducts.stream()
                .filter(p -> p.getKategori() != null)
                .map(p -> p.getKategori().getNamaKategori())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<Long> kategoriCounts = kategoriLabels.stream()
                .map(label -> allProducts.stream()
                        .filter(p -> p.getKategori() != null && label.equals(p.getKategori().getNamaKategori()))
                        .count())
                .collect(Collectors.toList());

        model.addAttribute("totalProduk", totalProduk);
        model.addAttribute("bisaDijualCount", bisaDijualCount);
        model.addAttribute("tidakBisaDijualCount", tidakBisaDijualCount);
        model.addAttribute("kategoriLabels", kategoriLabels);
        model.addAttribute("kategoriCounts", kategoriCounts);
        
        return "dashboard";
    }
}
