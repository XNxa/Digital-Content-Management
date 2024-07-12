export interface User {
  id: string | undefined;
  firstname: string;
  lastname: string;
  function: string;
  email: string;
  role: string;
  statut: 'active' | 'inactive';
  password: string;
}

export interface UserFilter {
  firstname: string | undefined;
  lastname: string | undefined;
  function: string | undefined;
  email: string | undefined;
  role: string | undefined;
  statut: 'active' | 'inactive' | undefined;
  password: undefined;
}
