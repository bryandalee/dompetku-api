import { useEffect, useState } from 'react'
import BudgetBar from '../components/BudgetBar.jsx'
import MonthSwitcher from '../components/MonthSwitcher.jsx'
import { SkeletonRows } from '../components/Skeleton.jsx'
import { api } from '../lib/api'
import { currentMonth } from '../lib/format'

export default function BudgetsPage() {
  const [month, setMonth] = useState(currentMonth())
  // null = kategori belum selesai dimuat
  const [categories, setCategories] = useState(null)
  const [budgets, setBudgets] = useState([])
  const [amounts, setAmounts] = useState({})
  const [reloadKey, setReloadKey] = useState(0)
  const [error, setError] = useState('')
  const [budgetsLoading, setBudgetsLoading] = useState(true)

  useEffect(() => {
    api('/api/categories')
      .then((data) => setCategories(data.filter((category) => category.type === 'EXPENSE')))
      .catch((err) => {
        setError(err.message)
        setCategories([])
      })
  }, [])

  useEffect(() => {
    api(`/api/budgets?month=${month}`)
      .then((data) => {
        setBudgets(data)
        const filled = {}
        data.forEach((budget) => {
          filled[budget.categoryId] = String(Number(budget.amount))
        })
        setAmounts(filled)
      })
      .catch((err) => setError(err.message))
      .finally(() => setBudgetsLoading(false))
  }, [month, reloadKey])

  function changeMonth(newMonth) {
    setBudgetsLoading(true)
    setMonth(newMonth)
  }

  async function saveBudget(event, categoryId) {
    event.preventDefault()
    setError('')
    try {
      await api('/api/budgets', {
        method: 'PUT',
        body: { categoryId, month, amount: Number(amounts[categoryId]) },
      })
      setReloadKey((key) => key + 1)
    } catch (err) {
      setError(err.errors?.amount ?? err.message)
    }
  }

  async function deleteBudget(budget) {
    setError('')
    try {
      await api(`/api/budgets/${budget.id}`, { method: 'DELETE' })
      setReloadKey((key) => key + 1)
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="space-y-6">
      <header className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">Budget</h1>
          <p className="mt-1 text-sm text-muted">Batas pengeluaran per kategori untuk bulan yang dipilih.</p>
        </div>
        <MonthSwitcher month={month} onChange={changeMonth} />
      </header>

      {error && <p className="rounded-lg bg-expense/10 px-4 py-3 text-sm text-expense">{error}</p>}

      {categories === null || budgetsLoading ? (
        <SkeletonRows count={5} />
      ) : (
        <ul className="divide-y divide-line rounded-2xl border border-line bg-white">
          {categories.map((category) => {
            const budget = budgets.find((item) => item.categoryId === category.id)
            return (
              <li key={category.id} className="grid gap-4 px-4 py-4 md:grid-cols-[1fr_auto] md:items-center">
                <div className="min-w-0">
                  {budget ? (
                    <BudgetBar budget={budget} />
                  ) : (
                    <>
                      <p className="text-sm font-semibold">{category.name}</p>
                      <p className="text-xs text-muted">Belum ada budget</p>
                    </>
                  )}
                </div>
                <form onSubmit={(event) => saveBudget(event, category.id)} className="flex items-center gap-2">
                  <input
                    type="number"
                    min="1"
                    className="input amount w-36"
                    value={amounts[category.id] ?? ''}
                    onChange={(event) => setAmounts({ ...amounts, [category.id]: event.target.value })}
                    placeholder="Rp"
                    aria-label={`Budget ${category.name}`}
                  />
                  <button type="submit" className="btn-primary">Simpan</button>
                  {budget && (
                    <button type="button" className="btn-danger-link" onClick={() => deleteBudget(budget)}>
                      Hapus
                    </button>
                  )}
                </form>
              </li>
            )
          })}
        </ul>
      )}
    </div>
  )
}
