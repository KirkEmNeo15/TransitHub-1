import axios from 'axios'
import { tokenStorage } from '../utils/tokenStorage'

// One shared Axios instance. Every request goes to the backend address below.
// The address can be changed with VITE_API_BASE_URL in frontend/.env.
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

// Before every request: add "Authorization: Bearer <token>" when the user is logged in.
api.interceptors.request.use((config) => {
  const token = tokenStorage.get()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// After every response: if the server says 401 for a request that carried a token,
// the token is expired or invalid. Forget it and tell the app to log the user out.
api.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    if (axios.isAxiosError(error) && error.response?.status === 401 && tokenStorage.get()) {
      tokenStorage.clear()
      window.dispatchEvent(new Event('auth:expired'))
    }
    return Promise.reject(error)
  },
)

export default api
