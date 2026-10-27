import { formatMonth, shiftMonth } from '../lib/format'

export default function MonthSwitcher({ month, onChange }) {
  return (
    <div className="inline-flex items-center gap-1 rounded-lg border border-line bg-white p-1">
      <button
        type="button"
        onClick={() => onChange(shiftMonth(month, -1))}
        className="rounded-md px-2.5 py-1 text-lg leading-none text-muted hover:bg-paper hover:text-ink"
        aria-label="Bulan sebelumnya"
      >
        ‹
      </button>
      <span className="min-w-36 text-center text-sm font-semibold">{formatMonth(month)}</span>
      <button
        type="button"
        onClick={() => onChange(shiftMonth(month, 1))}
        className="rounded-md px-2.5 py-1 text-lg leading-none text-muted hover:bg-paper hover:text-ink"
        aria-label="Bulan berikutnya"
      >
        ›
      </button>
    </div>
  )
}
