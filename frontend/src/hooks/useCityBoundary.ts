import type { CityBoundary } from '../utils/boundary'
import { parseBoundary } from '../utils/boundary'
import { useApiData } from './useApiData'

// The official Batangas City boundary (PSA PSGC 2023), served by Vite from frontend/public/data.
const BOUNDARY_URL = '/data/batangas-city-boundary.geojson'

let cached: Promise<CityBoundary> | null = null

/** Loads the file once per page visit; later calls reuse the same answer. */
function loadBoundary(): Promise<CityBoundary> {
  if (!cached) {
    cached = fetch(BOUNDARY_URL)
      .then((response) => {
        if (!response.ok) throw new Error(`The city boundary could not be loaded (HTTP ${response.status}).`)
        return response.json() as Promise<unknown>
      })
      .then(parseBoundary)
      .catch((error: unknown) => {
        cached = null // allow "Try again"
        throw error
      })
  }
  return cached
}

/** The Batangas City boundary: loading, success (the boundary) or error. */
export function useCityBoundary() {
  return useApiData(loadBoundary)
}