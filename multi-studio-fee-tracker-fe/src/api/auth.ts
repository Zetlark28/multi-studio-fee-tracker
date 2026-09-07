import { http } from './http';

export interface AuthResponse {
  token: string;
  username: string;
  admin: boolean;
}

export const authApi = {
  login: (username: string, password: string) => http.post<AuthResponse>('/auth/login', { username, password }),
  changePassword: (username: string, oldPassword: string, newPassword: string) =>
    http.post<void>('/auth/change-password', { username, oldPassword, newPassword }),
};
