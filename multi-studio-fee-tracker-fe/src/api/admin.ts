import { http } from './http';

export interface AppUserSummary {
  username: string;
  role: 'USER' | 'ADMIN';
}

const RESOURCE = '/admin/users';

export const adminApi = {
  listUsers: () => http.get<AppUserSummary[]>(RESOURCE),
  createUser: (username: string, password: string) =>
    http.post<AppUserSummary>(RESOURCE, { username, password }),
  setPassword: (username: string, password: string) =>
    http.put<void>(`${RESOURCE}/${encodeURIComponent(username)}/password`, { password }),
};
