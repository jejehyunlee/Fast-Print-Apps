package com.fastprint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProdukDto {
    @JsonProperty("id_produk")
    private String idProduk;

    @JsonProperty("nama_produk")
    private String namaProduk;

    @JsonProperty("harga")
    private String harga;

    @JsonProperty("kategori")
    private String kategori;

    @JsonProperty("status")
    private String status;
}
