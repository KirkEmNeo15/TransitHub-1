import { AxiosError } from 'axios'
import type { AxiosResponse } from 'axios'
import { describe, expect, it } from 'vitest'
import { parseApiError } from './apiError'

function errorWithResponse(status: number, data: unknown): AxiosError {
  const response = { status, data, statusText: '', headers: {}, config: {} } as AxiosResponse
  return new AxiosError('Request failed', 'ERR_BAD_REQUEST', undefined, undefined, response)
}

describe('parseApiError', () => {
  it('reads the message and field errors sent by the backend', () => {
    const parsed = parseApiError(
      errorWithResponse(400, {
        status: 400,
        message: 'Validation failed',
        errors: [{ field: 'email', message: 'Email must be valid' }],
      }),
    )
    expect(parsed.message).toBe('Validation failed')
    expect(parsed.fieldErrors).toEqual({ email: 'Email must be valid' })
  })

  it('works when the backend sends no field errors', () => {
    const parsed = parseApiError(errorWithResponse(409, { status: 409, message: 'Stop is used by a route' }))
    expect(parsed.message).toBe('Stop is used by a route')
    expect(parsed.fieldErrors).toEqual({})
  })

  it('falls back to the status code when the answer is not our error format', () => {
    expect(parseApiError(errorWithResponse(500, '<html>oops</html>')).message).toContain('500')
  })

  it('explains when the server cannot be reached', () => {
    const noResponse = new AxiosError('Network Error', 'ERR_NETWORK')
    expect(parseApiError(noResponse).message).toContain('Cannot reach the server')
  })

  it('handles errors that are not from Axios', () => {
    expect(parseApiError(new Error('boom')).message).toBe('Something went wrong.')
  })
})
