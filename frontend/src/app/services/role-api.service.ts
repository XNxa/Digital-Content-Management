import { Injectable } from "@angular/core";
import { environment } from "../../environments/environment.development";
import { HttpClient } from "@angular/common/http";
import { Role } from "../models/Role";
import { Permission } from "../models/Permission";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class RoleApiService {

    private API = environment.api + '/role';

    constructor(private http: HttpClient) { }

    public getRoles(firstResult: number, maxResults: number, filter: Role): Observable<Role[]> {
        return this.http.post<any>(`${this.API}/roles`, filter,
            {
                params: { firstResult: firstResult.toString(), maxResults: maxResults.toString() }
            });
    }

    public getRole(id: string): Observable<Role> {
        return this.http.get<Role>(`${this.API}/role`, { params: { id: id } });
    }

    public countRoles(): Observable<number> {
        return this.http.get<number>(`${this.API}/count`);
    }

    public deleteRole(id: string): Observable<void> {
        return this.http.delete<void>(`${this.API}/delete`, { params: { id: id } });
    }

    public updateRole(role: Role): Observable<void> {
        return this.http.put<void>(`${this.API}/update`, role);
    }

    public createRole(role: Role): Observable<void> {
        return this.http.post<void>(`${this.API}/create`, role);
    }

    public getPermissions(): Observable<Permission[]> {
        return this.http.get<Permission[]>(`${this.API}/permissions`);
    }

    public getActiveRoles(): Observable<string[]> {
        return this.http.get<string[]>(`${this.API}/actives`);
    }
}