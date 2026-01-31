package com.fastprint.repository;

import com.fastprint.entity.Kategori;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KategoriRepository extends JpaRepository<Kategori, Long> {
    Optional<Kategori> findByNamaKategori(String namaKategori);
}
