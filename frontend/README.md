# DompetKu Web

Frontend React untuk DompetKu, aplikasi pencatat keuangan pribadi. Backend-nya ada di root repo ini
(lihat [README utama](../README.md)).

## Fitur

- Daftar dan masuk akun (token JWT disimpan di browser)
- Ringkasan bulanan: saldo, pemasukan, pengeluaran, grafik per kategori, status budget
- Daftar transaksi dengan filter bulan dan kategori, pagination, tambah, ubah, dan hapus
- Kelola kategori dan budget per kategori

## Tech stack

React 19, Vite, React Router, Tailwind CSS 4, Recharts.

## Menjalankan di komputer sendiri

Butuh Node.js 24 dan backend yang sedang berjalan di `localhost:8080`.

```bash
npm install
npm run dev
```

Buka http://localhost:5173. Alamat backend diatur lewat variabel `VITE_API_URL` di file `.env.development`.

## Build untuk produksi

```bash
npm run build
```

Hasilnya ada di folder `dist/`. Saat deploy di Vercel, isi `VITE_API_URL` dengan alamat backend online,
dan set **Root Directory** ke `frontend`.
