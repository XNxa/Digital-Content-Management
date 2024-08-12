export interface Role {
  id: string | undefined;
  name: string;
  description: string;
  state: boolean;
  printableState?: 'Actif' | 'Inactif';
  permissions: string[];
}
