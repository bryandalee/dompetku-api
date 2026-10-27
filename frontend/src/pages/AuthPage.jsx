import { useState } from 'react'
import { Link, Navigate } from 'react-router'
import { useAuth } from '../context/AuthContext.jsx'

export default function AuthPage({ mode }) {
  const isRegister = mode === 'register'
  const { user, login, register } = useAuth()
  const [form, setForm] = useState({ name: '', email: '', password: '' })
  const [errors, setErrors] = useState({})
  const [message, setMessage] = useState('')
  const [submitting, setSubmitting] = useState(false)

  if (user) {
    return <Navigate to="/" replace />
  }

  function updateField(field, value) {
    setForm((current) => ({ ...current, [field]: value }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setSubmitting(true)
    setErrors({})
    setMessage('')
    try {
      if (isRegister) {
        await register(form.name, form.email.trim(), form.password)
      } else {
        await login(form.email.trim(), form.password)
      }
    } catch (error) {
      setErrors(error.errors ?? {})
      setMessage(error.message)
      setSubmitting(false)
    }
  }

  return (
    <div className="grid min-h-screen lg:grid-cols-2">
      <section className="hidden flex-col justify-between bg-brand p-12 text-white lg:flex">
        <span className="text-2xl font-extrabold tracking-tight">DompetKu</span>
        <div>
          <p className="text-4xl leading-tight font-bold">Tahu ke mana uangmu pergi, setiap bulan.</p>
          <p className="mt-4 max-w-md text-white/75">
            Catat pemasukan dan pengeluaran, atur budget per kategori, dan lihat ringkasan bulananmu dalam satu tempat.
          </p>
        </div>
        <p className="text-sm text-white/60">Dibuat dengan Spring Boot dan React.</p>
      </section>

      <section className="flex items-center justify-center px-4 py-12">
        <div className="w-full max-w-sm">
          <span className="mb-8 block text-2xl font-extrabold tracking-tight text-brand lg:hidden">DompetKu</span>
          <h1 className="text-2xl font-bold">{isRegister ? 'Buat akun baru' : 'Masuk ke akunmu'}</h1>
          <p className="mt-1 text-sm text-muted">
            {isRegister ? 'Kategori dasar langsung disiapkan untukmu.' : 'Selamat datang kembali.'}
          </p>

          <form onSubmit={handleSubmit} className="mt-8 space-y-4">
            {isRegister && (
              <div>
                <label htmlFor="auth-name" className="label">Nama</label>
                <input
                  id="auth-name"
                  className="input"
                  value={form.name}
                  onChange={(event) => updateField('name', event.target.value)}
                  autoComplete="name"
                />
                {errors.name && <p className="mt-1 text-xs text-expense">{errors.name}</p>}
              </div>
            )}

            <div>
              <label htmlFor="auth-email" className="label">Email</label>
              <input
                id="auth-email"
                type="email"
                className="input"
                value={form.email}
                onChange={(event) => updateField('email', event.target.value)}
                autoComplete="email"
              />
              {errors.email && <p className="mt-1 text-xs text-expense">{errors.email}</p>}
            </div>

            <div>
              <label htmlFor="auth-password" className="label">Password</label>
              <input
                id="auth-password"
                type="password"
                className="input"
                value={form.password}
                onChange={(event) => updateField('password', event.target.value)}
                autoComplete={isRegister ? 'new-password' : 'current-password'}
              />
              {errors.password && <p className="mt-1 text-xs text-expense">{errors.password}</p>}
            </div>

            {message && <p className="rounded-lg bg-expense/10 px-3 py-2 text-sm text-expense">{message}</p>}

            <button type="submit" disabled={submitting} className="btn-primary w-full py-2.5">
              {submitting ? 'Memproses...' : isRegister ? 'Daftar' : 'Masuk'}
            </button>
          </form>

          <p className="mt-6 text-sm text-muted">
            {isRegister ? 'Sudah punya akun? ' : 'Belum punya akun? '}
            <Link to={isRegister ? '/login' : '/register'} className="btn-link">
              {isRegister ? 'Masuk' : 'Daftar sekarang'}
            </Link>
          </p>
        </div>
      </section>
    </div>
  )
}
