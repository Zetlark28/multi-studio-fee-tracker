import { clearSession, getToken } from './session';

const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api';

export class ApiError extends Error {
  readonly status: number;

  constructor(status: number, message: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
  }
}

interface ErrorResponseBody {
  errorMessage?: string;
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const token = getToken();
  const headers: Record<string, string> = { 'Content-Type': 'application/json' };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(`${BASE_URL}${path}`, { headers, ...init });

  if (response.status === 401) {
    clearSession();
  }

  if (!response.ok) {
    const body: ErrorResponseBody | null = await response.json().catch(() => null);
    throw new ApiError(response.status, body?.errorMessage ?? response.statusText);
  }

  const hasBody = response.status !== 204 && response.headers.get('content-length') !== '0';
  return hasBody ? ((await response.json()) as T) : (undefined as T);
}

export const http = {
  get: <T>(path: string) => request<T>(path),
  post: <T>(path: string, body: unknown) => request<T>(path, { method: 'POST', body: JSON.stringify(body) }),
  put: <T>(path: string, body: unknown) => request<T>(path, { method: 'PUT', body: JSON.stringify(body) }),
  remove: (path: string) => request<void>(path, { method: 'DELETE' }),
};

export function toErrorMessage(error: unknown): string {
  return error instanceof ApiError ? error.message : 'Si è verificato un errore imprevisto.';
}
