import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment.development";
import { User, UserFilter } from "../models/User";

@Injectable({
    providedIn: 'root'
})
export class UserApiService {

    private API = environment.api + '/user';

    constructor(private http: HttpClient) { }

    public getNumberOfUser(): Observable<number> {
        return this.http.get<number>(`${this.API}/count`);
    }

    public getUsers(firstResult: number, maxResults: number, filter: UserFilter): Observable<User[]> {
        const params = new HttpParams().set('firstResult', firstResult.toString()).set('maxResults', maxResults.toString());
        return this.http.post<User[]>(`${this.API}/list`, filter, { params });
    }

    public createUser(user: User): Observable<void> {
        return this.http.post<void>(`${this.API}/create`, user);
    }

    public deleteUser(email: string): Observable<void> {
        return this.http.delete<void>(`${this.API}/delete}`, {
            params: {
                email: email
            }
        });
    }

    public updateUser(user: User): Observable<void> {
        return this.http.put<void>(`${this.API}/update`, user);
    }
}