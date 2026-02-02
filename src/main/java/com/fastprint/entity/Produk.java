package com.fastprint.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "produk")
@Data
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.SQLRestriction("deleted_at IS NULL")
public class Produk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produk")
    private Long idProduk;

    @NotBlank(message = "Nama produk harus diisi")
    @Column(name = "nama_produk", nullable = false, length = 200)
    private String namaProduk;

    @NotNull(message = "Harga harus diisi")
    @Positive(message = "Harga harus berupa angka positif")
    @Column(name = "harga", nullable = false)
    private Double harga;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kategori_id", nullable = false)
    @NotNull(message = "Kategori harus dipilih")
    private Kategori kategori;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id", nullable = false)
    @NotNull(message = "Status harus dipilih")
    private Status status;

    @Column(name = "created_at", updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    @org.hibernate.annotations.UpdateTimestamp
    private java.time.LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;
}
