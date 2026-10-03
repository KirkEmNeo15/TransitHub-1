import axios from 'axios'

// One shared Axios instance. Every request goes to the backend address below.
// The address can be changed with VITE_API_BASE_URL in frontend/.env.
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

export default api
