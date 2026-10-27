# DompetKu

Aplikasi pencatat keuangan pribadi: pemasukan, pengeluaran, budget per kategori, dan ringkasan bulanan.
Backend REST API dibangun dengan **Java 25** dan **Spring Boot 4**, frontend dengan **React** (folder [`frontend/`](frontend)).

![CI](https://github.com/bryandalee/dompetku-api/actions/workflows/ci.yml/badge.svg)

## Fitur

- Register dan login dengan **JWT**; setiap user hanya bisa melihat datanya sendiri
- CRUD **kategori** (pemasukan / pengeluaran), dengan kategori bawaan saat register
- CRUD **transaksi** dengan filter bulan dan kategori, plus **pagination**
- **Budget** per kategori per bulan, dengan status `SAFE`, `WARNING`, atau `EXCEEDED`
- **Laporan bulanan**: total pemasukan, pengeluaran, saldo, dan rincian per kategori
- Validasi input dan format error yang seragam
- Dokumentasi API interaktif dengan **Swagger UI**
- Frontend React: ringkasan dengan grafik, daftar transaksi, kategori, dan budget
- Unit test dan integration test, dijalankan otomatis oleh **GitHub Actions**

## Tech stack

| Bagian | Teknologi |
|---|---|
| Bahasa | Java 25, JavaScript |
| Backend | Spring Boot 4.1 (Web MVC, Data JPA, Validation, Security) |
| Autentikasi | Spring Security OAuth2 Resource Server (JWT, HS256) |
| Database | PostgreSQL 18 (test memakai H2) |
| Frontend | React 19, Vite, React Router, Tailwind CSS 4, Recharts |
| Dokumentasi | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5, Mockito, AssertJ, MockMvc |
| DevOps | Docker, Docker Compose, GitHub Actions |

## Struktur repo

```
dompetku/
├── src/main/java/com/bryan/dompetku/   backend Spring Boot
│   ├── controller/   endpoint HTTP
│   ├── service/      aturan bisnis
│   ├── repository/   akses database
│   ├── entity/       tabel database
│   ├── dto/          bentuk data request dan response
│   ├── security/     JWT, CORS, aturan akses
│   ├── exception/    error buatan sendiri dan handler global
│   └── config/       Swagger dan data demo
├── src/test/         unit test dan integration test
├── frontend/         aplikasi React
├── Dockerfile, docker-compose.yml
└── .github/workflows/ci.yml
```

## Arsitektur

```
Frontend React (frontend/)
        │  HTTP + JSON, header Authorization: Bearer <token>
        ▼
Controller   → menerima request, validasi (@Valid), memanggil service
Service      → aturan bisnis, @Transactional
Repository   → Spring Data JPA (derived query, JPQL)
        ▼
PostgreSQL
```

Error dari semua lapisan ditangkap oleh `GlobalExceptionHandler` dan dikembalikan dalam format yang sama:

```json
{ "status": 404, "message": "Kategori dengan id 99 tidak ditemukan", "errors": {}, "timestamp": "..." }
```

## Skema database

```
users (id, name, email, password)
  │ 1
  ├──< categories (id, name, type, user_id)
  │        │ 1
  │        ├──< transactions (id, amount, transaction_date, note, category_id, user_id)
  │        └──< budgets (id, amount, month_start, category_id, user_id)
```

## Menjalankan backend dengan Docker (paling mudah)

Butuh Docker Desktop. Perintah ini menjalankan PostgreSQL dan API sekaligus, lengkap dengan akun demo:

```bash
docker compose up --build
```

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Akun demo: `demo@dompetku.com` / `demo12345`

## Menjalankan backend tanpa Docker

Butuh JDK 25 dan PostgreSQL dengan database bernama `dompetku`.

1. Salin `.env.example` menjadi `.env`, lalu isi `DB_PASSWORD` dan `JWT_SECRET`. File `.env` tidak ikut ke Git.
2. Jalankan:

```bash
./mvnw spring-boot:run
```

Semua pengaturan juga bisa diisi lewat environment variable (dipakai di server):

| Variabel | Contoh | Wajib |
|---|---|---|
| `DB_PASSWORD` | password user postgres | ya |
| `JWT_SECRET` | teks acak minimal 32 karakter | ya |
| `DB_URL` | `jdbc:postgresql://localhost:5432/dompetku` | tidak |
| `DB_USERNAME` | `postgres` | tidak |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | tidak |
| `SPRING_PROFILES_ACTIVE` | `demo` (membuat akun demo) | tidak |
| `PORT` | `8080` | tidak |

## Menjalankan frontend

Butuh Node.js 24 dan backend yang sedang berjalan di `localhost:8080`.

```bash
cd frontend
npm install
npm run dev
```

Buka http://localhost:5173. Alamat backend diatur lewat `VITE_API_URL` di `frontend/.env.development`.

## Menjalankan test

```bash
./mvnw test
```

Test memakai database H2 di memori, jadi tidak perlu PostgreSQL.

## Endpoint utama

| Method | Endpoint | Keterangan |
|---|---|---|
| POST | `/api/auth/register` | Daftar akun baru |
| POST | `/api/auth/login` | Login, mendapat token JWT |
| GET | `/api/auth/me` | Data user yang sedang login |
| GET/POST | `/api/categories` | Daftar / tambah kategori |
| PUT/DELETE | `/api/categories/{id}` | Ubah / hapus kategori |
| GET | `/api/transactions?month=2026-10&categoryId=1&page=0&size=10` | Daftar transaksi |
| POST | `/api/transactions` | Tambah transaksi |
| PUT/DELETE | `/api/transactions/{id}` | Ubah / hapus transaksi |
| GET | `/api/budgets?month=2026-10` | Budget bulan tertentu beserta pemakaiannya |
| PUT | `/api/budgets` | Buat atau ubah budget |
| DELETE | `/api/budgets/{id}` | Hapus budget |
| GET | `/api/reports/monthly?month=2026-10` | Ringkasan bulanan |

Daftar lengkap beserta contoh request ada di Swagger UI.
