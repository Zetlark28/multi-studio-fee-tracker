import { http } from './http';
import type { ClientBillableService, ResponseList } from '../types';

const RESOURCE = '/client-billable-services';

export const clientBillableServicesApi = {
  listByClient: (clientId: number) =>
    http.get<ResponseList<ClientBillableService>>(`${RESOURCE}?clientId=${clientId}&size=200`),
  create: (clientId: number, name: string, price: number) =>
    http.post<ClientBillableService>(RESOURCE, { clientId, name, price }),
  remove: (id: number) => http.remove(`${RESOURCE}/${id}`),
};
