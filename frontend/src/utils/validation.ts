// The same rules as the backend (RegisterRequest). The backend always checks again:
// these checks only give the user quick feedback.

export function validateRequired(value: string, label: string): string {
  return value.trim() === '' ? `${label} cannot be empty` : ''
}

export function validateEmail(value: string): string {
  if (value.trim() === '') return 'Email cannot be empty'
  const looksLikeEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value.trim())
  return looksLikeEmail ? '' : 'Email must be a valid email address'
}

export function validatePassword(value: string): string {
  if (value === '') return 'Password cannot be empty'
  if (value.length < 8 || value.length > 72) return 'Password must be between 8 and 72 characters'
  if (!/[A-Za-z]/.test(value) || !/\d/.test(value)) {
    return 'Password must contain at least one letter and one number'
  }
  return ''
}
