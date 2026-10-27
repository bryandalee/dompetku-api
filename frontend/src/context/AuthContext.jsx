import { createContext, useContext, useEffect, useState } from 'react'
import { api, clearToken, getToken, saveToken } from '../lib/api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(() => Boolean(getToken()))

  useEffect(() => {
    if (!getToken()) return
    api('/api/auth/me')
      .then(setUser)
      .catch(() => clearToken())
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    const handleLogout = () => setUser(null)
    window.addEventListener('dompetku:logout', handleLogout)
    return () => window.removeEventListener('dompetku:logout', handleLogout)
  }, [])

  async function login(email, password) {
    const data = await api('/api/auth/login', { method: 'POST', body: { email, password } })
    saveToken(data.token)
    setUser(data.user)
  }

  async function register(name, email, password) {
    const data = await api('/api/auth/register', { method: 'POST', body: { name, email, password } })
    saveToken(data.token)
    setUser(data.user)
  }

  function logout() {
    clearToken()
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
