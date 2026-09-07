import { http } from './http';
import type { ResponseList, ServiceSelection, UserClientActivity } from '../types';

const RESOURCE = '/user-client-activities';

interface UserClientActivityPayload {
  clientId: number;
  date: number;
  quantity?: number;
  services?: ServiceSelection[];
}

export const userClientActivitiesApi = {
  listByClient: (clientId: number) =>
    http.get<ResponseList<UserClientActivity>>(`${RESOURCE}?clientId=${clientId}&size=200`),
  listForMonth: (month: number) => http.get<UserClientActivity[]>(`${RESOURCE}/history?month=${month}`),
  create: (payload: UserClientActivityPayload) => http.post<UserClientActivity>(RESOURCE, payload),
  remove: (id: number) => http.remove(`${RESOURCE}/${id}`),
};
