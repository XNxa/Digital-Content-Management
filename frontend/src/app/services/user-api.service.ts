import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.development';
import { User, UserFilter } from '../models/User';

@Injectable({
  providedIn: 'root',
})
export class UserApiService {
  private API = environment.api + '/user';

  constructor(private http: HttpClient) {}

  public getNumberOfUser(): Observable<number> {
    return this.http.get<number>(`${this.API}/count`);
  }

  public getUsers(
    firstResult: number,
    maxResults: number,
    filter: UserFilter,
  ): Observable<User[]> {
    const params = new HttpParams()
      .set('firstResult', firstResult.toString())
      .set('maxResults', maxResults.toString());
    return this.http.post<User[]>(`${this.API}/list`, filter, { params });
  }

  public createUser(user: User): Observable<void> {
    return this.http.post<void>(`${this.API}/create`, user);
  }

  public deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API}/delete`, {
      params: { id: id },
    });
  }

  public updateUser(user: User): Observable<void> {
    return this.http.put<void>(`${this.API}/update`, user);
  }

  public getUser(id: string): Observable<User> {
    return this.http.get<User>(`${this.API}/user`, {
      params: {
        id: id,
      },
    });
  }

  public getFunctions(): Observable<string[]> {
    return this.http.get<string[]>(`${this.API}/functions`);
  }

  public validateEmail(email: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.API}/validate`, {
      params: { email: email },
    });
  }

  public validateCredentials(username: string, password: string) {
    const body = {
      username,
      password
    }
    return this.http.post<boolean>(`${this.API}/checkpassword`, body);
  }
}
