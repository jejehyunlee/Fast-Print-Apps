# 💠 Fast-Print-Apps: Premium Product Management System

[![Live Demo](https://img.shields.io/badge/Live%20Demo-Railway-blueviolet?style=for-the-badge&logo=railway)](https://fast-print-apps-production.up.railway.app/dashboard)
[![GitHub Repo](https://img.shields.io/badge/Source%20Code-GitHub-black?style=for-the-badge&logo=github)](https://github.com/jejehyunlee/Fast-Print-Apps)

**Fast-Print-Apps** adalah sistem manajemen produk modern yang dirancang dengan estetika premium dan fungsionalitas tinggi. Project ini merupakan solusi komprehensif untuk pengelolaan data produk yang terintegrasi langsung dengan API eksternal Fast Print, dilengkapi dengan visualisasi data yang interaktif.

---

## 🔗 Quick Links
- **Publish URL (Railway)**: [https://fast-print-apps-production.up.railway.app/dashboard](https://fast-print-apps-production.up.railway.app/dashboard)
- **Source Code (GitHub)**: [https://github.com/jejehyunlee/Fast-Print-Apps](https://github.com/jejehyunlee/Fast-Print-Apps)

---

## ✨ Fitur Utama

### 📊 1. Dashboard Analytics Premium
Visualisasi data secara *real-time* yang memberikan *insight* instan mengenai status inventory:
- **Doughnut Chart**: Distribusi produk berdasarkan status (Bisa Dijual vs Tidak Bisa Dijual).
- **Bar Chart**: Statistik jumlah produk per kategori.

### 📦 2. Full CRUD Operations
Manajemen produk yang mudah dan aman:
- **Tabel Interaktif**: Menggunakan *DataTables* untuk pencarian cepat, sorting, dan paging.
- **Form Validasi**: Validasi ketat pada sisi server dan client (Nama tidak boleh kosong, harga harus angka).
- **Modal System**: Proses tambah dan edit data dilakukan melalui modal tanpa reload.
- **SweetAlert2**: Notifikasi dan konfirmasi hapus yang cantik.

### 🔄 3. Smart API Synchronization
Sistem integrasi otomatis dengan API Recruitment Fast Print:
- **Dynamic Authentication**: Username dihasilkan otomatis berdasarkan format waktu WIB terbaru.
- **MD5 Security**: Implementasi algoritma hash MD5 untuk password.

### 🎨 4. Modern User Interface
Desain yang "Wow" dengan fokus pada estetika:
- **Glassmorphism Design**: Panel-panel transparan yang elegan.
- **Responsive Layout**: Optimal untuk diakses melalui berbagai perangkat.

---

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot 3.2.1
- **Database**: PostgreSQL
- **Frontend Engine**: Thymeleaf
- **Styling**: Bootstrap 5, Custom Premium CSS
- **Libraries**: Lombok, Chart.js, DataTables, SweetAlert2, WebFlux
- **Deployment**: Railway

---

## 🚀 Instalasi Lokal

1. Clone repository ini.
2. Buat database `fastprint_db` di PostgreSQL.
3. Buat file `.env` di root folder:
   ```properties
   DATABASE_URL=postgresql://localhost:5432/fastprint_db
   PGUSER=postgres
   PGPASSWORD=password_anda
   API_PRODUCTS_URL=https://recruitment.fastprint.co.id/tes/api_tes_programmer
   ```
4. Jalankan perintah: `mvn spring-boot:run`.

---

## 📂 Struktur Project

- `src/main/java/com/fastprint/controller`: Handler HTTP.
- `src/main/java/com/fastprint/service`: Bisnis logic & API.
- `src/main/resources/templates`: Tampilan UI (Thymeleaf).
- `src/main/resources/static/css`: Styling premium.

---
© 2026 Developed by [Jefri Saputra](https://github.com/jejehyunlee)
