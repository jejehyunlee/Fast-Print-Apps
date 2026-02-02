package com.fastprint.repository;

import com.fastprint.entity.Produk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdukRepository extends JpaRepository<Produk, Long> {

    // Find products where the status name is exactly "bisa dijual"
    @Query("SELECT p FROM Produk p WHERE p.status.namaStatus = 'bisa dijual'")
    List<Produk> findByStatusBisaDijual();

    // Standard find by status method if needed
    List<Produk> findByStatus_NamaStatus(String namaStatus);

    // Find by product name for deduplication
    Produk findByNamaProduk(String namaProduk);
}
