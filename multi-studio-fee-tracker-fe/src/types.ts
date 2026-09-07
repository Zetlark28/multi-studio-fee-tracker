export type FeeType = 'DAILY' | 'PERCENT';

export interface Client {
  id: number;
  name: string;
  fee: number | null;
  type: FeeType | null;
  createdAt: string;
  createdBy: string | null;
  updatedAt: string;
  updatedBy: string | null;
}

export interface ClientBillableService {
  id: number;
  clientId: number;
  name: string;
  price: number;
  createdAt: string;
  createdBy: string | null;
  updatedAt: string;
  updatedBy: string | null;
}

export interface ServiceSelection {
  serviceId: number;
  quantity: number;
}

export interface UserClientActivity {
  id: number;
  clientId: number;
  services: ServiceSelection[];
  date: number;
  quantity: number | null;
  price: number | null;
  fee: number;
  createdAt: string;
  createdBy: string | null;
  updatedAt: string;
  updatedBy: string | null;
}

export interface ResponseList<T> {
  data: T[];
  totalItems: number;
}

export interface DashboardData {
  totalRevenue: number | null;
  workDays: number | null;
  month: number | null;
  totalClients: number | null;
}
