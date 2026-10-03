import { Link, Route, Routes } from 'react-router'
import Home from './pages/Home'
import NotFound from './pages/NotFound'
import SetupCheck from './pages/SetupCheck'

// Temporary navigation and routes. Phase 12 replaces this with the real layout
// (Navbar, Sidebar, all pages and protected admin routes).
export default function App() {
  return (
    <div className="min-h-screen">
      <header className="bg-secondary text-white">
        <nav className="mx-auto flex max-w-5xl items-center gap-6 px-4 py-3">
          <span className="text-lg font-bold">TransitHub</span>
          <Link to="/" className="hover:text-blue-300">Home</Link>
          <Link to="/setup-check" className="hover:text-blue-300">Setup check</Link>
        </nav>
      </header>

      <main className="mx-auto max-w-5xl px-4 py-8">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/setup-check" element={<SetupCheck />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </main>
    </div>
  )
}
