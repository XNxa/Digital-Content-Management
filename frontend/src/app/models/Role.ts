export interface Role {
  id: string | undefined;
  name: string;
  description: string;
  state: boolean;
  permissions: string[];
}
