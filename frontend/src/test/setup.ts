import { cleanup } from '@testing-library/react'
import { afterEach } from 'vitest'

// After every test, remove the components it drew so tests do not affect each other.
afterEach(() => {
  cleanup()
})
