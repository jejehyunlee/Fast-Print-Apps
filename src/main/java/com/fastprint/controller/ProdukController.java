package com.fastprint.controller;

import com.fastprint.entity.Kategori;
import com.fastprint.entity.Produk;
import com.fastprint.entity.Status;
import com.fastprint.service.ProdukService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/produk")
public class ProdukController {

    @Autowired
    private ProdukService produkService;

    @ModelAttribute("kategoris")
    public List<Kategori> populateKategoris() {
        return produkService.findAllKategori();
    }

    @ModelAttribute("statuses")
    public List<Status> populateStatuses() {
        return produkService.findAllStatus();
    }

    @GetMapping
    public String listProduk(@RequestParam(value = "statusFilter", required = false) String statusFilter, Model model) {
        List<Produk> produks;
        if (statusFilter != null && !statusFilter.isEmpty()) {
            if ("semua".equalsIgnoreCase(statusFilter)) {
                produks = produkService.findAllProduk();
            } else {
                produks = produkService.findByStatus(statusFilter);
            }
        } else {
            // Default: show 'bisa dijual'
            produks = produkService.findProdukBisaDijual();
            statusFilter = "bisa dijual";
        }

        model.addAttribute("produks", produks);
        model.addAttribute("selectedStatus", statusFilter);
        model.addAttribute("produk", new Produk()); // For Modal
        return "list";
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public Produk getProdukJson(@PathVariable("id") Long id) {
        return produkService.getProdukById(id);
    }

    @PostMapping("/save")
    public String saveProduk(@ModelAttribute("produk") Produk produk, BindingResult result,
            Model model, RedirectAttributes redirectAttributes) {
        // Basic Validation
        if (produk.getNamaProduk() == null || produk.getNamaProduk().trim().isEmpty()) {
            result.rejectValue("namaProduk", "error.produk", "Nama Produk tidak boleh kosong");
        }
        if (produk.getHarga() == null) {
            result.rejectValue("harga", "error.produk", "Harga tidak boleh kosong");
        } else if (produk.getHarga() < 0) {
            result.rejectValue("harga", "error.produk", "Harga tidak boleh negatif");
        }

        if (result.hasErrors()) {
            // Re-populate list data for the 'list' view
            List<Produk> produks = produkService.findProdukBisaDijual();
            model.addAttribute("produks", produks);
            model.addAttribute("selectedStatus", "bisa dijual");
            return "list";
        }

        produkService.saveProduk(produk);
        redirectAttributes.addFlashAttribute("message", "Produk berhasil disimpan!");
        return "redirect:/produk";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Produk produk = produkService.getProdukById(id);
        model.addAttribute("produk", produk);
        return "form";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduk(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        produkService.deleteProduk(id);
        redirectAttributes.addFlashAttribute("message", "Produk berhasil dihapus!");
        return "redirect:/produk";
    }

    @GetMapping("/sync")
    public String syncData(RedirectAttributes redirectAttributes) {
        produkService.syncDataFromApi();
        redirectAttributes.addFlashAttribute("message", "Sinkronisasi data berhasil!");
        return "redirect:/produk";
    }
}
