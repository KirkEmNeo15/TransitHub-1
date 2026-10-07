import { beforeEach, describe, expect, it } from 'vitest'
import { tokenStorage } from './tokenStorage'

describe('tokenStorage', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('has no token at first', () => {
    expect(tokenStorage.get()).toBeNull()
  })
  it('saves and reads the token', () => {
    tokenStorage.set('abc.def.ghi')
    expect(tokenStorage.get()).toBe('abc.def.ghi')
  })
  it('forgets the token on clear (logout)', () => {
    tokenStorage.set('abc')
    tokenStorage.clear()
    expect(tokenStorage.get()).toBeNull()
  })
})
