import { http } from './http';
import type { DashboardData } from '../types';

export const dashboardApi = {
  getData: (month: number) => http.get<DashboardData>(`/dashboard?month=${month}`),
};
