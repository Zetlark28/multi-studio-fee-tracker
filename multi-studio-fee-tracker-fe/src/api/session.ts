const TOKEN_KEY = 'authToken';
const USERNAME_KEY = 'authUsername';
const ADMIN_KEY = 'authIsAdmin';

type Listener = () => void;

const listeners = new Set<Listener>();

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function getUsername(): string | null {
  return localStorage.getItem(USERNAME_KEY);
}

export function getIsAdmin(): boolean {
  return localStorage.getItem(ADMIN_KEY) === 'true';
}

export function setSession(token: string, username: string, isAdmin: boolean): void {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USERNAME_KEY, username);
  localStorage.setItem(ADMIN_KEY, String(isAdmin));
  notify();
}

export function clearSession(): void {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USERNAME_KEY);
  localStorage.removeItem(ADMIN_KEY);
  notify();
}

export function subscribe(listener: Listener): () => void {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

function notify(): void {
  listeners.forEach((listener) => listener());
}
