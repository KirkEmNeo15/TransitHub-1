// The one error format every backend error uses (see "Errors" in docs/API.md).
export interface FieldErrorDetail {
  field: string
  message: string
}

export interface ApiError {
  status: number
  message: string
  timestamp: string
  path: string
  errors: FieldErrorDetail[]
}
