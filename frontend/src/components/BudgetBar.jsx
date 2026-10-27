import { formatRupiah } from '../lib/format'

const STATUS_STYLE = {
  SAFE: { label: 'Aman', bar: 'bg-brand', text: 'text-brand' },
  WARNING: { label: 'Hampir habis', bar: 'bg-warn', text: 'text-warn' },
  EXCEEDED: { label: 'Melebihi budget', bar: 'bg-expense', text: 'text-expense' },
}

export default function BudgetBar({ budget }) {
  const style = STATUS_STYLE[budget.status]
  const width = Math.min(Number(budget.percentage), 100)

  return (
    <div>
      <div className="flex items-baseline justify-between gap-3">
        <span className="text-sm font-semibold">{budget.categoryName}</span>
        <span className={`text-xs font-semibold ${style.text}`}>
          {style.label} · {budget.percentage}%
        </span>
      </div>
      <div className="mt-2 h-2 overflow-hidden rounded-full bg-line">
        <div className={`h-full rounded-full ${style.bar}`} style={{ width: `${width}%` }} />
      </div>
      <p className="amount mt-1.5 text-xs text-muted">
        {formatRupiah(budget.spent)} dari {formatRupiah(budget.amount)}
      </p>
    </div>
  )
}
