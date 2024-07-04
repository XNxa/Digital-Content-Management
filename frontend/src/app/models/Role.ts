export interface Role {
    name: string;
    description: string;
    state: boolean;
    permissions: string[];
}