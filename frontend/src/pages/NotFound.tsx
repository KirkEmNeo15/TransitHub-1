import { Link } from 'react-router'

export default function NotFound() {
  return (
    <section className="py-16 text-center">
      <p className="text-6xl font-bold text-primary">404</p>
      <h1 className="mt-2 text-2xl font-bold">Page not found</h1>
      <Link to="/" className="mt-4 inline-block text-primary underline">
        Back to the home page
      </Link>
    </section>
  )
}
