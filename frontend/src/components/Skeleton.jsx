// Kotak abu-abu berdenyut sebagai pengganti konten selama data dari API belum datang.
// Pakai `light` di atas latar gelap (misalnya kartu saldo berwarna brand).
export default function Skeleton({ className = '', light = false }) {
  return (
    <div
      className={`animate-pulse rounded-md ${light ? 'bg-white/20' : 'bg-line'} ${className}`}
      aria-hidden="true"
    />
  )
}

// Meniru bentuk daftar transaksi/kategori: judul dan keterangan di kiri, angka di kanan.
export function SkeletonRows({ count = 4 }) {
  return (
    <ul className="divide-y divide-line rounded-2xl border border-line bg-white" aria-busy="true" aria-label="Memuat data">
      {Array.from({ length: count }, (_, index) => (
        <li key={index} className="flex items-center justify-between gap-4 px-4 py-3.5">
          <div className="space-y-2">
            <Skeleton className="h-4 w-40" />
            <Skeleton className="h-3 w-24" />
          </div>
          <Skeleton className="h-4 w-20" />
        </li>
      ))}
    </ul>
  )
}

// Meniru bentuk BudgetBar: nama kategori, batang progres, dan keterangan nominal.
export function SkeletonBars({ count = 3 }) {
  return (
    <div className="space-y-5" aria-busy="true" aria-label="Memuat data">
      {Array.from({ length: count }, (_, index) => (
        <div key={index}>
          <div className="flex justify-between">
            <Skeleton className="h-4 w-28" />
            <Skeleton className="h-3 w-20" />
          </div>
          <Skeleton className="mt-2 h-2 w-full rounded-full" />
          <Skeleton className="mt-2 h-3 w-36" />
        </div>
      ))}
    </div>
  )
}
