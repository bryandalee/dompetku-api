const rupiahFormatter = new Intl.NumberFormat('id-ID', {
  style: 'currency',
  currency: 'IDR',
  maximumFractionDigits: 0,
})

export function formatRupiah(value) {
  return rupiahFormatter.format(Number(value ?? 0))
}

export function formatDate(isoDate) {
  const [year, month, day] = isoDate.split('-').map(Number)
  return new Date(year, month - 1, day).toLocaleDateString('id-ID', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
}

function toMonthString(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
}

export function currentMonth() {
  return toMonthString(new Date())
}

export function shiftMonth(month, delta) {
  const [year, monthNumber] = month.split('-').map(Number)
  return toMonthString(new Date(year, monthNumber - 1 + delta, 1))
}

export function formatMonth(month) {
  const [year, monthNumber] = month.split('-').map(Number)
  return new Date(year, monthNumber - 1, 1).toLocaleDateString('id-ID', {
    month: 'long',
    year: 'numeric',
  })
}

export function todayIso() {
  const now = new Date()
  return `${toMonthString(now)}-${String(now.getDate()).padStart(2, '0')}`
}
