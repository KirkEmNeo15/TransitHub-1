// The login token is kept in the browser's localStorage so a page refresh does not log you out.
// (Trade-off: a cookie marked httpOnly would be safer against XSS, but then the API would need CSRF
// protection. For this school project we use the simpler header-token approach.)
const TOKEN_KEY = 'transithub.token'

export const tokenStorage = {
  get(): string | null {
    return localStorage.getItem(TOKEN_KEY)
  },
  set(token: string): void {
    localStorage.setItem(TOKEN_KEY, token)
  },
  clear(): void {
    localStorage.removeItem(TOKEN_KEY)
  },
}
