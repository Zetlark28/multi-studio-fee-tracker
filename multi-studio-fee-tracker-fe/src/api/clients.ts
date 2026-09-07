import { http } from './http';
import type { Client, FeeType, ResponseList } from '../types';

const RESOURCE = '/clients';

export const clientsApi = {
  list: () => http.get<ResponseList<Client>>(`${RESOURCE}?size=200`),
  getById: (id: number) => http.get<Client>(`${RESOURCE}/${id}`),
  create: (name: string, fee: number | null = null, type: FeeType | null = null) =>
    http.post<Client>(RESOURCE, { name, fee, type }),
  update: (id: number, name: string, fee: number | null, type: FeeType | null) =>
    http.put<Client>(`${RESOURCE}/${id}`, { name, fee, type }),
  remove: (id: number) => http.remove(`${RESOURCE}/${id}`),
};
