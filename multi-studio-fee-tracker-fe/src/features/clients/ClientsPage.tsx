import { useState } from 'react';
import { ClientList } from './ClientList';
import { ClientDetail } from './ClientDetail';

export function ClientsPage() {
  const [selectedClientId, setSelectedClientId] = useState<number | null>(null);

  if (selectedClientId !== null) {
    return <ClientDetail clientId={selectedClientId} onBack={() => setSelectedClientId(null)} />;
  }

  return <ClientList onSelectClient={setSelectedClientId} />;
}
