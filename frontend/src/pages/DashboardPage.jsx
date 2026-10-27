import { useEffect, useState } from 'react'
import { Link } from 'react-router'
import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from 'recharts'
import BudgetBar from '../components/BudgetBar.jsx'
import MonthSwitcher from '../components/MonthSwitcher.jsx'
import { api } from '../lib/api'
import { currentMonth, formatDate, formatRupiah } from '../lib/format'

const CHART_COLORS = ['#135e5a', '#2f7fa8', '#c77d1a', '#8e4fa0', '#b42318', '#4f7a28', '#6b6f80', '#d0567a']

export default function DashboardPage() {
  const [month, setMonth] = useState(currentMonth())
  const [summary, setSummary] = useState(null)
  const [budgets, setBudgets] = useState([])
  const [recent, setRecent] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    Promise.all([
      api(`/api/reports/monthly?month=${month}`),
      api(`/api/budgets?month=${month}`),
      api(`/api/transactions?month=${month}&size=5`),
    ])
      .then(([summaryData, budgetData, transactionPage]) => {
        setError('')
        setSummary(summaryData)
        setBudgets(budgetData)
        setRecent(transactionPage.content)
      })
      .catch((err) => setError(err.message))
  }, [month])

  const expenseData = (summary?.categories ?? [])
    .filter((category) => category.type === 'EXPENSE')
    .map((category) => ({ name: category.categoryName, value: Number(category.total) }))

  return (
    <div className="space-y-8">
      <header className="flex flex-wrap items-center justify-between gap-4">
        <h1 className="text-2xl font-bold">Ringkasan</h1>
        <MonthSwitcher month={month} onChange={setMonth} />
      </header>

      {error && <p className="rounded-lg bg-expense/10 px-4 py-3 text-sm text-expense">{error}</p>}

      <section className="rounded-2xl bg-brand p-6 text-white sm:p-8">
        <p className="text-sm text-white/75">Saldo bulan ini</p>
        <p className="amount mt-1 text-4xl font-extrabold tracking-tight sm:text-5xl">
          {formatRupiah(summary?.balance)}
        </p>
        <div className="mt-6 grid gap-4 border-t border-white/20 pt-5 sm:grid-cols-2">
          <div>
            <p className="text-sm text-white/75">Pemasukan</p>
            <p className="amount text-xl font-bold">{formatRupiah(summary?.totalIncome)}</p>
          </div>
          <div>
            <p className="text-sm text-white/75">Pengeluaran</p>
            <p className="amount text-xl font-bold">{formatRupiah(summary?.totalExpense)}</p>
          </div>
        </div>
      </section>

      <div className="grid gap-8 lg:grid-cols-2">
        <section>
          <h2 className="mb-4 text-lg font-bold">Pengeluaran per kategori</h2>
          {expenseData.length === 0 ? (
            <p className="text-sm text-muted">Belum ada pengeluaran di bulan ini.</p>
          ) : (
            <div className="flex flex-col items-center gap-4 sm:flex-row">
              <div className="h-48 w-48 shrink-0">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie data={expenseData} dataKey="value" nameKey="name" innerRadius={50} outerRadius={85} paddingAngle={2}>
                      {expenseData.map((entry, index) => (
                        <Cell key={entry.name} fill={CHART_COLORS[index % CHART_COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip formatter={(value) => formatRupiah(value)} />
                  </PieChart>
                </ResponsiveContainer>
              </div>
              <ul className="w-full space-y-2">
                {expenseData.map((entry, index) => (
                  <li key={entry.name} className="flex items-center justify-between gap-3 text-sm">
                    <span className="flex items-center gap-2">
                      <span
                        className="h-2.5 w-2.5 rounded-full"
                        style={{ backgroundColor: CHART_COLORS[index % CHART_COLORS.length] }}
                      />
                      {entry.name}
                    </span>
                    <span className="amount font-semibold">{formatRupiah(entry.value)}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </section>

        <section>
          <div className="mb-4 flex items-center justify-between">
            <h2 className="text-lg font-bold">Budget</h2>
            <Link to="/budgets" className="btn-link">Atur budget</Link>
          </div>
          {budgets.length === 0 ? (
            <p className="text-sm text-muted">Belum ada budget untuk bulan ini.</p>
          ) : (
            <div className="space-y-5">
              {budgets.map((budget) => (
                <BudgetBar key={budget.id} budget={budget} />
              ))}
            </div>
          )}
        </section>
      </div>

      <section>
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-lg font-bold">Transaksi terbaru</h2>
          <Link to="/transactions" className="btn-link">Lihat semua</Link>
        </div>
        {recent.length === 0 ? (
          <p className="text-sm text-muted">Belum ada transaksi di bulan ini.</p>
        ) : (
          <ul className="divide-y divide-line rounded-2xl border border-line bg-white">
            {recent.map((transaction) => (
              <li key={transaction.id} className="flex items-center justify-between gap-4 px-4 py-3">
                <div className="min-w-0">
                  <p className="truncate text-sm font-semibold">{transaction.note || transaction.category.name}</p>
                  <p className="text-xs text-muted">
                    {transaction.category.name} · {formatDate(transaction.transactionDate)}
                  </p>
                </div>
                <span
                  className={`amount shrink-0 text-sm font-bold ${
                    transaction.category.type === 'INCOME' ? 'text-income' : 'text-expense'
                  }`}
                >
                  {transaction.category.type === 'INCOME' ? '+' : '−'}
                  {formatRupiah(transaction.amount)}
                </span>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  )
}
