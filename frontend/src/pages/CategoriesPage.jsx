import { useEffect, useState } from 'react'
import { api } from '../lib/api'

const TYPE_LABEL = { INCOME: 'Pemasukan', EXPENSE: 'Pengeluaran' }

export default function CategoriesPage() {
  const [categories, setCategories] = useState([])
  const [newCategory, setNewCategory] = useState({ name: '', type: 'EXPENSE' })
  const [editing, setEditing] = useState(null)
  const [error, setError] = useState('')

  function loadCategories() {
    api('/api/categories').then(setCategories).catch((err) => setError(err.message))
  }

  useEffect(loadCategories, [])

  async function runAction(action) {
    setError('')
    try {
      await action()
      loadCategories()
      return true
    } catch (err) {
      setError(err.errors?.name ?? err.message)
      return false
    }
  }

  async function handleCreate(event) {
    event.preventDefault()
    const created = await runAction(() => api('/api/categories', { method: 'POST', body: newCategory }))
    if (created) setNewCategory({ name: '', type: newCategory.type })
  }

  async function handleUpdate(event) {
    event.preventDefault()
    const updated = await runAction(() =>
      api(`/api/categories/${editing.id}`, { method: 'PUT', body: { name: editing.name, type: editing.type } }),
    )
    if (updated) setEditing(null)
  }

  function handleDelete(category) {
    if (!window.confirm(`Hapus kategori "${category.name}"?`)) return
    runAction(() => api(`/api/categories/${category.id}`, { method: 'DELETE' }))
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Kategori</h1>

      <form onSubmit={handleCreate} className="flex flex-wrap items-end gap-3 rounded-2xl border border-line bg-white p-4">
        <div className="min-w-48 flex-1">
          <label htmlFor="new-category-name" className="label">Nama kategori baru</label>
          <input
            id="new-category-name"
            className="input"
            value={newCategory.name}
            onChange={(event) => setNewCategory({ ...newCategory, name: event.target.value })}
            placeholder="Contoh: Pendidikan"
          />
        </div>
        <div>
          <label htmlFor="new-category-type" className="label">Jenis</label>
          <select
            id="new-category-type"
            className="input"
            value={newCategory.type}
            onChange={(event) => setNewCategory({ ...newCategory, type: event.target.value })}
          >
            <option value="EXPENSE">Pengeluaran</option>
            <option value="INCOME">Pemasukan</option>
          </select>
        </div>
        <button type="submit" className="btn-primary">Tambah kategori</button>
      </form>

      {error && <p className="rounded-lg bg-expense/10 px-4 py-3 text-sm text-expense">{error}</p>}

      {['INCOME', 'EXPENSE'].map((type) => (
        <section key={type}>
          <h2 className="mb-3 text-lg font-bold">{TYPE_LABEL[type]}</h2>
          <ul className="divide-y divide-line rounded-2xl border border-line bg-white">
            {categories
              .filter((category) => category.type === type)
              .map((category) =>
                editing?.id === category.id ? (
                  <li key={category.id} className="px-4 py-3">
                    <form onSubmit={handleUpdate} className="flex flex-wrap items-center gap-2">
                      <input
                        className="input min-w-40 flex-1"
                        value={editing.name}
                        onChange={(event) => setEditing({ ...editing, name: event.target.value })}
                        aria-label="Nama kategori"
                        autoFocus
                      />
                      <select
                        className="input w-auto"
                        value={editing.type}
                        onChange={(event) => setEditing({ ...editing, type: event.target.value })}
                        aria-label="Jenis kategori"
                      >
                        <option value="EXPENSE">Pengeluaran</option>
                        <option value="INCOME">Pemasukan</option>
                      </select>
                      <button type="submit" className="btn-primary">Simpan</button>
                      <button type="button" className="btn-secondary" onClick={() => setEditing(null)}>Batal</button>
                    </form>
                  </li>
                ) : (
                  <li key={category.id} className="flex items-center justify-between gap-4 px-4 py-3">
                    <span className="text-sm font-semibold">{category.name}</span>
                    <div className="flex gap-3">
                      <button type="button" className="btn-link" onClick={() => setEditing({ ...category })}>Ubah</button>
                      <button type="button" className="btn-danger-link" onClick={() => handleDelete(category)}>
                        Hapus
                      </button>
                    </div>
                  </li>
                ),
              )}
          </ul>
        </section>
      ))}
    </div>
  )
}
