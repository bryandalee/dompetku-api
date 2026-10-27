import { useState } from 'react'
import { todayIso } from '../lib/format'

export default function TransactionForm({ categories, initialValue, onSubmit, onCancel }) {
  const [form, setForm] = useState({
    amount: initialValue?.amount ?? '',
    transactionDate: initialValue?.transactionDate ?? todayIso(),
    note: initialValue?.note ?? '',
    categoryId: initialValue?.category.id ?? categories[0]?.id ?? '',
  })
  const [errors, setErrors] = useState({})
  const [message, setMessage] = useState('')
  const [saving, setSaving] = useState(false)

  function updateField(field, value) {
    setForm((current) => ({ ...current, [field]: value }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setSaving(true)
    setErrors({})
    setMessage('')
    try {
      await onSubmit({
        amount: Number(form.amount),
        transactionDate: form.transactionDate,
        note: form.note.trim() || null,
        categoryId: Number(form.categoryId),
      })
    } catch (error) {
      setErrors(error.errors ?? {})
      setMessage(error.message)
      setSaving(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="tx-category" className="label">Kategori</label>
        <select
          id="tx-category"
          className="input"
          value={form.categoryId}
          onChange={(event) => updateField('categoryId', event.target.value)}
        >
          {categories.map((category) => (
            <option key={category.id} value={category.id}>
              {category.name} ({category.type === 'INCOME' ? 'Pemasukan' : 'Pengeluaran'})
            </option>
          ))}
        </select>
        {errors.categoryId && <p className="mt-1 text-xs text-expense">{errors.categoryId}</p>}
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <div>
          <label htmlFor="tx-amount" className="label">Jumlah (Rp)</label>
          <input
            id="tx-amount"
            type="number"
            min="1"
            step="any"
            className="input amount"
            value={form.amount}
            onChange={(event) => updateField('amount', event.target.value)}
            placeholder="25000"
          />
          {errors.amount && <p className="mt-1 text-xs text-expense">{errors.amount}</p>}
        </div>
        <div>
          <label htmlFor="tx-date" className="label">Tanggal</label>
          <input
            id="tx-date"
            type="date"
            className="input"
            value={form.transactionDate}
            onChange={(event) => updateField('transactionDate', event.target.value)}
          />
          {errors.transactionDate && <p className="mt-1 text-xs text-expense">{errors.transactionDate}</p>}
        </div>
      </div>

      <div>
        <label htmlFor="tx-note" className="label">Catatan (opsional)</label>
        <input
          id="tx-note"
          className="input"
          value={form.note}
          onChange={(event) => updateField('note', event.target.value)}
          placeholder="Contoh: makan siang di kantin"
        />
        {errors.note && <p className="mt-1 text-xs text-expense">{errors.note}</p>}
      </div>

      {message && <p className="rounded-lg bg-expense/10 px-3 py-2 text-sm text-expense">{message}</p>}

      <div className="flex justify-end gap-2">
        <button type="button" onClick={onCancel} className="btn-secondary">Batal</button>
        <button type="submit" disabled={saving} className="btn-primary">
          {saving ? 'Menyimpan...' : 'Simpan transaksi'}
        </button>
      </div>
    </form>
  )
}
