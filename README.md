# 💠 Fast-Print-Apps: Premium Product Management System

[![Live Demo](https://img.shields.io/badge/Live%20Demo-Railway-blueviolet?style=for-the-badge&logo=railway)](https://fast-print-apps-production.up.railway.app/dashboard)
[![GitHub Repo](https://img.shields.io/badge/Source%20Code-GitHub-black?style=for-the-badge&logo=github)](https://github.com/jejehyunlee/Fast-Print-Apps)

**Fast-Print-Apps** adalah sistem manajemen produk modern yang dirancang dengan estetika premium dan fungsionalitas tinggi. Project ini merupakan solusi komprehensif untuk pengelolaan data produk yang terintegrasi langsung dengan API eksternal Fast Print, dilengkapi dengan visualisasi data yang interaktif dan asisten bertenaga AI.

---

## 🔗 Quick Links
- **Publish URL (Railway)**: [https://fast-print-apps-production.up.railway.app/dashboard](https://fast-print-apps-production.up.railway.app/dashboard)
- **Source Code (GitHub)**: [https://github.com/jejehyunlee/Fast-Print-Apps](https://github.com/jejehyunlee/Fast-Print-Apps)

---

## ✨ Fitur Utama

### 🤖 1. FastPrint AI Assistant (Premium Feature)
Asisten pintar bertenaga **Groq AI (Llama 3.1)** yang terintegrasi langsung dengan database sistem:
- **Analytic Insight**: AI dapat menghitung statistik produk, kategori terbanyak, hingga audit trail (produk terbaru/terlama).
- **Price Analysis**: Memberikan informasi produk termahal dan termurah secara real-time.
- **Operational Guide**: Panduan interaktif mengenai alur CRUD dan validasi harga/nama produk.
- **Markdown Response**: Jawaban rapi dengan format tebal, list, dan mata uang Rupiah.

### 📊 2. Dashboard Analytics Premium
Visualisasi data secara *real-time* yang memberikan *insight* instan mengenai status inventory:
- **Doughnut Chart**: Distribusi produk berdasarkan status (Bisa Dijual vs Tidak Bisa Dijual).
- **Bar Chart**: Statistik jumlah produk per kategori.

### 📦 3. Full CRUD Operations
Manajemen produk yang mudah dan aman:
- **Tabel Interaktif**: Menggunakan *DataTables* untuk pencarian cepat, sorting, dan paging.
- **Form Validasi**: Validasi ketat pada sisi server dan client (Nama tidak boleh kosong, harga harus angka positif).
- **Audit Trail**: Pencatatan otomatis waktu pembuatan (`created_at`) dan pembaruan (`updated_at`) produk.
- **Modal System**: Proses tambah dan edit data dilakukan melalui modal tanpa reload.

### 🔄 4. Smart API Synchronization
Sistem integrasi otomatis dengan API Recruitment Fast Print:
- **Dynamic Authentication**: Username dihasilkan otomatis berdasarkan format waktu WIB terbaru.
- **MD5 Security**: Implementasi algoritma hash MD5 untuk password.

---

## 🛠️ Tech Stack

- **AI Engine**: Groq AI (Llama-3.1-8b-instant)
- **Backend**: Java 17, Spring Boot 3.2.1
- **Database**: PostgreSQL
- **Frontend Engine**: Thymeleaf
- **Styling**: Bootstrap 5, Custom Premium CSS (Glassmorphism)
- **Libraries**: Marked.js (Markdown Parser), Lombok, Chart.js, DataTables, SweetAlert2, WebFlux, Dotenv
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
   GROQ_API_KEY=gsk_anda_disini
   ```
4. Jalankan perintah: `mvn spring-boot:run`.

---

## 📂 Struktur Project

- `src/main/java/com/fastprint/controller`: Handler HTTP & AI Controller.
- `src/main/java/com/fastprint/service`: Bisnis logic, API Sync, & AI Service.
- `src/main/java/com/fastprint/entity`: Model database (Produk, Kategori, Status) dengan Audit fields.
- `src/main/resources/templates`: Tampilan UI (Thymeleaf).
- `src/main/resources/static/css`: Styling premium.

---
© 2026 Developed by [Jefri Saputra](https://github.com/jejehyunlee)
