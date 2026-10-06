import type { Schedule } from '../types/Route'
import type { Transportation } from '../types/Transportation'

const pesoFormatter = new Intl.NumberFormat('en-PH', { style: 'currency', currency: 'PHP' })

/** 35.1 -> "₱35.10" */
export function formatPeso(amount: number | null | undefined): string {
  return amount === null || amount === undefined ? 'N/A' : pesoFormatter.format(amount)
}

/** "05:00:00" -> "5:00 AM" */
export function formatTime(time: string): string {
  const [hourText, minuteText] = time.split(':')
  const hour = Number(hourText)
  const minute = Number(minuteText)
  const suffix = hour >= 12 ? 'PM' : 'AM'
  const hour12 = hour % 12 === 0 ? 12 : hour % 12
  return `${hour12}:${String(minute).padStart(2, '0')} ${suffix}`
}

/** 45 -> "45 min", 90 -> "1 h 30 min" */
export function formatDuration(minutes: number): string {
  if (minutes < 60) return `${minutes} min`
  const hours = Math.floor(minutes / 60)
  const rest = minutes % 60
  return rest === 0 ? `${hours} h` : `${hours} h ${rest} min`
}

export function formatDistance(km: number): string {
  return `${km.toFixed(1)} km`
}

export function formatDateTime(isoDate: string): string {
  return new Date(isoDate).toLocaleString('en-PH', { dateStyle: 'medium', timeStyle: 'short' })
}

/** "5:00 AM - 10:00 PM" from the first schedule, or "N/A" when the route has none. */
export function operatingHours(schedules: Schedule[]): string {
  if (schedules.length === 0) return 'N/A'
  return `${formatTime(schedules[0].firstTrip)} - ${formatTime(schedules[0].lastTrip)}`
}

/** "5:00 AM - 10:00 PM, every 15 min, MON-SUN" */
export function describeSchedule(schedule: Schedule): string {
  return (
    `${formatTime(schedule.firstTrip)} - ${formatTime(schedule.lastTrip)}, ` +
    `every ${schedule.frequencyMinutes} min, ${schedule.daysOperating}`
  )
}

/** Turns the type-specific details of a transportation into readable lines. */
export function describeDetails(transportation: Transportation): string[] {
  const details = transportation.details
  const lines: string[] = []
  if (typeof details.airConditioned === 'boolean') {
    lines.push(details.airConditioned ? 'Air-conditioned' : 'Not air-conditioned')
  }
  if (typeof details.modernized === 'boolean') {
    lines.push(details.modernized ? 'Modernized jeepney' : 'Traditional jeepney')
  }
  if (typeof details.seatingCapacity === 'number') {
    lines.push(`${details.seatingCapacity} seats`)
  }
  return lines
}
