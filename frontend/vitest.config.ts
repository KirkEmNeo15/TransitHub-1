import { defineConfig } from 'vitest/config'

// Settings for the frontend tests (run them with: npm test).
// "jsdom" is a pretend browser, so tests can use localStorage and render React components.
export default defineConfig({
  test: {
    environment: 'jsdom',
    include: ['src/**/*.test.{ts,tsx}'],
    setupFiles: ['src/test/setup.ts'],
  },
})
