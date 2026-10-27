import { useEffect, useState } from 'react'
import Modal from '../components/Modal.jsx'
import MonthSwitcher from '../components/MonthSwitcher.jsx'
import { SkeletonRows } from '../components/Skeleton.jsx'
import TransactionForm from '../components/TransactionForm.jsx'
import { api } from '../lib/api'
import { currentMonth, formatDate, formatRupiah } from '../lib/format'

const PAGE_SIZE = 10

export default function TransactionsPage() {
  const [month, setMonth] = useState(currentMonth())
  const [categoryId, setCategoryId] = useState('')
  const [page, setPage] = useState(0)
  const [result, setResult] = useState(null)
  const [categories, setCategories] = useState([])
  const [editing, setEditing] = useState(null)
  const [reloadKey, setReloadKey] = useState(0)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api('/api/categories').then(setCategories).catch((err) => setError(err.message))
  }, [])

  useEffect(() => {
    const params = new URLSearchParams({ month, page, size: PAGE_SIZE })
    if (categoryId) params.set('categoryId', categoryId)
    api(`/api/transactions?${params}`)
      .then(setResult)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [month, categoryId, page, reloadKey])

  // Skeleton hanya muncul saat isi daftar berganti (bulan, kategori, halaman).
  // Setelah tambah/ubah/hapus, daftar lama tetap tampil sampai data baru datang.
  function changeMonth(newMonth) {
    setLoading(true)
    setMonth(newMonth)
    setPage(0)
  }

  function changeCategory(newCategoryId) {
    setLoading(true)
    setCategoryId(newCategoryId)
    setPage(0)
  }

  function goToPage(newPage) {
    setLoading(true)
    setPage(newPage)
  }

  async function saveTransaction(payload) {
    if (editing.id) {
      await api(`/api/transactions/${editing.id}`, { method: 'PUT', body: payload })
    } else {
      await api('/api/transactions', { method: 'POST', body: payload })
    }
    setEditing(null)
    setReloadKey((key) => key + 1)
  }

  async function deleteTransaction(transaction) {
    if (!window.confirm(`Hapus transaksi "${transaction.note || transaction.category.name}"?`)) return
    try {
      await api(`/api/transactions/${transaction.id}`, { method: 'DELETE' })
      setReloadKey((key) => key + 1)
    } catch (err) {
      setError(err.message)
    }
  }

  const transactions = result?.content ?? []

  return (
    <div className="space-y-6">
      <header className="flex flex-wrap items-center justify-between gap-4">
        <h1 className="text-2xl font-bold">Transaksi</h1>
        <button
          type="button"
          className="btn-primary"
          onClick={() => setEditing({})}
          disabled={categories.length === 0}
        >
          Tambah transaksi
        </button>
      </header>

      <div className="flex flex-wrap items-center gap-3">
        <MonthSwitcher month={month} onChange={changeMonth} />
        <select
          className="input w-auto"
          value={categoryId}
          onChange={(event) => changeCategory(event.target.value)}
          aria-label="Filter kategori"
        >
          <option value="">Semua kategori</option>
          {categories.map((category) => (
            <option key={category.id} value={category.id}>{category.name}</option>
          ))}
        </select>
      </div>

      {error && <p className="rounded-lg bg-expense/10 px-4 py-3 text-sm text-expense">{error}</p>}

      {loading ? (
        <SkeletonRows count={6} />
      ) : transactions.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-line px-6 py-12 text-center">
          <p className="font-semibold">Belum ada transaksi di sini.</p>
          <p className="mt-1 text-sm text-muted">Klik "Tambah transaksi" untuk mencatat yang pertama.</p>
        </div>
      ) : (
        <ul className="divide-y divide-line rounded-2xl border border-line bg-white">
          {transactions.map((transaction) => (
            <li key={transaction.id} className="flex flex-wrap items-center justify-between gap-x-4 gap-y-2 px-4 py-3">
              <div className="min-w-0 flex-1">
                <p className="truncate text-sm font-semibold">{transaction.note || transaction.category.name}</p>
                <p className="text-xs text-muted">
                  {transaction.category.name} · {formatDate(transaction.transactionDate)}
                </p>
              </div>
              <span
                className={`amount text-sm font-bold ${
                  transaction.category.type === 'INCOME' ? 'text-income' : 'text-expense'
                }`}
              >
                {transaction.category.type === 'INCOME' ? '+' : '−'}
                {formatRupiah(transaction.amount)}
              </span>
              <div className="flex gap-3">
                <button type="button" className="btn-link" onClick={() => setEditing(transaction)}>Ubah</button>
                <button type="button" className="btn-danger-link" onClick={() => deleteTransaction(transaction)}>
                  Hapus
                </button>
              </div>
            </li>
          ))}
        </ul>
      )}

      {result && result.totalPages > 1 && (
        <div className="flex items-center justify-between text-sm">
          <span className="text-muted">
            Halaman {result.page + 1} dari {result.totalPages} · {result.totalElements} transaksi
          </span>
          <div className="flex gap-2">
            <button type="button" className="btn-secondary" disabled={page === 0} onClick={() => goToPage(page - 1)}>
              Sebelumnya
            </button>
            <button
              type="button"
              className="btn-secondary"
              disabled={page + 1 >= result.totalPages}
              onClick={() => goToPage(page + 1)}
            >
              Berikutnya
            </button>
          </div>
        </div>
      )}

      {editing && (
        <Modal title={editing.id ? 'Ubah transaksi' : 'Tambah transaksi'} onClose={() => setEditing(null)}>
          <TransactionForm
            categories={categories}
            initialValue={editing.id ? editing : null}
            onSubmit={saveTransaction}
            onCancel={() => setEditing(null)}
          />
        </Modal>
      )}
    </div>
  )
}
