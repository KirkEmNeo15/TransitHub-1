import { Route, Routes } from 'react-router'
import ProtectedRoute from './components/ProtectedRoute'
import AdminLayout from './layouts/AdminLayout'
import MainLayout from './layouts/MainLayout'
import Alerts from './pages/Alerts'
import Favorites from './pages/Favorites'
import Home from './pages/Home'
import Login from './pages/Login'
import MapPage from './pages/Map'
import NotFound from './pages/NotFound'
import Profile from './pages/Profile'
import Register from './pages/Register'
import RouteDetails from './pages/RouteDetails'
import RoutesPage from './pages/Routes'
import AlertsManagement from './pages/admin/AlertsManagement'
import Dashboard from './pages/admin/Dashboard'
import RoutesManagement from './pages/admin/RoutesManagement'
import StopsManagement from './pages/admin/StopsManagement'
import TransportationManagement from './pages/admin/TransportationManagement'
import UsersManagement from './pages/admin/UsersManagement'

// All the URLs of the app.
export default function App() {
  return (
    <Routes>
      {/* public pages, inside the Navbar + Footer frame */}
      <Route element={<MainLayout />}>
        <Route index element={<Home />} />
        <Route path="map" element={<MapPage />} />
        <Route path="routes" element={<RoutesPage />} />
        <Route path="routes/:id" element={<RouteDetails />} />
        <Route path="alerts" element={<Alerts />} />
        <Route path="login" element={<Login />} />
        <Route path="register" element={<Register />} />

        {/* pages that need a login */}
        <Route element={<ProtectedRoute />}>
          <Route path="favorites" element={<Favorites />} />
          <Route path="profile" element={<Profile />} />
        </Route>

        <Route path="*" element={<NotFound />} />
      </Route>

      {/* admin pages: login + ADMIN role, inside the Sidebar frame */}
      <Route path="admin" element={<ProtectedRoute requireAdmin />}>
        <Route element={<AdminLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="routes" element={<RoutesManagement />} />
          <Route path="stops" element={<StopsManagement />} />
          <Route path="transportation" element={<TransportationManagement />} />
          <Route path="alerts" element={<AlertsManagement />} />
          <Route path="users" element={<UsersManagement />} />
        </Route>
      </Route>
    </Routes>
  )
}
