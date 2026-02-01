# 💠 Fast-Print-Apps: Premium Product Management System

[![Live Demo](https://img.shields.io/badge/Live%20Demo-Railway-blueviolet?style=for-the-badge&logo=railway)](https://fast-print-apps-production.up.railway.app/dashboard)
[![GitHub Repo](https://img.shields.io/badge/Source%20Code-GitHub-black?style=for-the-badge&logo=github)](https://github.com/jejehyunlee/Fast-Print-Apps)

**Fast-Print-Apps** adalah sistem manajemen produk modern yang dirancang dengan estetika premium dan fungsionalitas tinggi. Project ini merupakan solusi komprehensif untuk pengelolaan data produk yang terintegrasi langsung dengan API eksternal Fast Print, dilengkapi dengan visualisasi data yang interaktif.

---

## 🔗 Quick Links
- **Publish URL (Railway)**: [https://fast-print-apps-production.up.railway.app/dashboard](https://fast-print-apps-production.up.railway.app/dashboard)
- **Source Code (GitHub)**: [https://github.com/jejehyunlee/Fast-Print-Apps](https://github.com/jejehyunlee/Fast-Print-Apps)

---

## ✨ Fitur Unggulan

### 📊 1. Dashboard Analytics Premium
Visualisasi data secara *real-time* yang memberikan *insight* instan mengenai status inventory:
- **Doughnut Chart**: Distribusi produk berdasarkan status (Bisa Dijual vs Tidak Bisa Dijual).
- **Bar Chart**: Statistik jumlah produk per kategori.
- **Micro-Animations**: Efek hover dan transisi halus untuk pengalaman pengguna yang lebih baik.

### 📦 2. Full CRUD Operations
Manajemen produk yang mudah dan aman:
- **Tabel Interaktif**: Menggunakan *DataTables* untuk pencarian cepat, sorting, dan paging.
- **Form Validasi**: Validasi ketat pada sisi server dan client (Nama tidak boleh kosong, harga harus angka).
- **Modal System**: Proses tambah dan edit data dilakukan melalui modal tanpa perlu reload halaman.
- **Soft Confirmation**: Penghapusan data dilengkapi dengan konfirmasi *SweetAlert2* untuk mencegah kesalahan.

### 🔄 3. Smart API Synchronization
Sistem integrasi otomatis dengan API Recruitment Fast Print:
- **Dynamic Authentication**: Username dihasilkan otomatis berdasarkan format waktu WIB terbaru.
- **MD5 Security**: Implementasi algoritma hash MD5 untuk password sesuai spesifikasi API.
- **Conflict Handling**: Logika penyimpanan cerdas yang menghindari duplikasi kategori dan status di database.

### 🎨 4. Modern User Interface
Desain yang "Wow" dengan fokus pada estetika:
- **Glassmorphism Design**: Panel-panel transparan dengan latar belakang gradien yang elegan.
- **Dark/Light Mode Ready**: Skema warna yang nyaman di mata dengan kontras tinggi.
- **Responsive Layout**: Optimal untuk diakses melalui desktop maupun perangkat mobile.

---

## 🛠️ Tech Stack & Tooling

| Layer | Technology |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot 3.2.1 |
| **Persistence** | Spring Data JPA, Hibernate, PostgreSQL |
| **Frontend Engine** | Thymeleaf Template Engine |
| **Styling** | Bootstrap 5, Vanilla CSS (Premium Custom), Google Fonts |
| **Libraries** | Lombok, Chart.js, DataTables, SweetAlert2, WebFlux |
| **Security** | MD5 Hashing, Dynamic Dynamic Auth generation |
| **Deployment** | Railway PAAS, GitHub Actions |

---

## 🏗️ Architecture & Database Schema

Aplikasi ini mengikuti pola arsitektur **MVC (Model-View-Controller)** yang terpisah dengan rapi:

### Database Relationships:
- **Produk**: Tabel utama yang menyimpan detail produk (nama, harga).
- **Kategori**: Tabel referensi kategori (Many-to-One dengan Produk).
- **Status**: Tabel referensi status (Many-to-One dengan Produk).

```sql
-- Skema Dasar
produk (id_produk, nama_produk, harga, kategori_id, status_id)
kategori (id_kategori, nama_kategori)
status (id_status, nama_status)
```

---

## 🚀 Panduan Instalasi Lokal

### 1. Prasyarat
- JDK 17 atau lebih tinggi.
- Maven 3.x.
- PostgreSQL Database.

### 2. Setup Database
Buat database di PostgreSQL:
```sql
CREATE DATABASE fastprint_db;
```

### 3. Konfigurasi Environment
Buat file `.env` di direktori root:
```properties
DATABASE_URL=postgresql://localhost:5432/fastprint_db
PGUSER=postgres
PGPASSWORD=password_kamu
API_PRODUCTS_URL=https://recruitment.fastprint.co.id/tes/api_tes_programmer
```

### 4. Build & Run
```bash
mvn clean install
mvn spring-boot:run
```
Buka [http://localhost:8080](http://localhost:8080) di browser Anda.

---

## ☁️ Deployment Configuration (Railway)

Project ini dikonfigurasi untuk *Zero-Config Deployment* di Railway:
1. **system.properties**: Diatur ke `java.runtime.version=17`.
2. **Environment Variables**: Menggunakan variabel sistem `SPRING_DATASOURCE_URL`, `PGUSER`, `PGPASSWORD`, dan `PGHOST` yang disediakan secara otomatis oleh Railway PostgreSQL Plugin.
3. **CI/CD**: Setiap push ke branch `main` akan memicu build otomatis via GitHub Actions.

---

## 📂 Struktur Direktori

```text
Fast-Print-Apps/
├── src/main/java/com/fastprint/
│   ├── controller/    # HTTP Request Handlers
│   ├── entity/        # JPA Entities (Database Models)
│   ├── repository/    # Data Access Layer
│   └── service/       # Business Logic & API Client
├── src/main/resources/
│   ├── static/        # CSS, JS, Images
│   └── templates/      # Thymeleaf HTML Templates
├── .github/workflows/ # Automation scripts
├── pom.xml            # Project dependencies
└── DOCUMENTATION.md   # Current File
```

---

## 👨‍💻 Developer Note
Aplikasi ini dikembangkan dengan fokus pada **Clean Code** dan **User Experience**. Setiap komponen frontend dipoles untuk memberikan kesan aplikasi enterprise yang premium.

---
© 2026 Developed by [Jeje Hyun Lee](https://github.com/jejehyunlee)
