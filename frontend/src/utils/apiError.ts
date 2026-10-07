import axios from 'axios'
import type { ApiError } from '../types/ApiError'

export interface ParsedApiError {
  message: string
  // field name -> message, for showing errors under the matching form field
  fieldErrors: Record<string, string>
}

function isApiError(data: unknown): data is ApiError {
  return (
    typeof data === 'object' &&
    data !== null &&
    'message' in data &&
    typeof (data as { message: unknown }).message === 'string'
  )
}

/** Turns any error thrown by Axios into a message we can show to the user. */
export function parseApiError(error: unknown): ParsedApiError {
  if (axios.isAxiosError(error)) {
    if (!error.response) {
      return {
        message: 'Cannot reach the server. Check that the backend is running.',
        fieldErrors: {},
      }
    }
    const data: unknown = error.response.data
    if (isApiError(data)) {
      const fieldErrors: Record<string, string> = {}
      for (const fieldError of data.errors ?? []) {
        fieldErrors[fieldError.field] = fieldError.message
      }
      return { message: data.message, fieldErrors }
    }
    return { message: `The server answered with status ${error.response.status}.`, fieldErrors: {} }
  }
  return { message: 'Something went wrong.', fieldErrors: {} }
}
