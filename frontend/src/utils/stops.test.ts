import { describe, expect, it } from 'vitest'
import { isTerminal } from './stops'

describe('isTerminal', () => {
  it('is true when the name contains "terminal", in any case', () => {
    expect(isTerminal({ name: 'Batangas Grand Terminal' })).toBe(true)
    expect(isTerminal({ name: 'BALAGTAS TERMINAL' })).toBe(true)
  })
  it('is false for other stops', () => {
    expect(isTerminal({ name: 'Balagtas' })).toBe(false)
    expect(isTerminal({ name: 'Batangas City Proper' })).toBe(false)
  })
})