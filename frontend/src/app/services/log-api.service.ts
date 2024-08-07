import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Log } from '../models/Log';

@Injectable({
  providedIn: 'root',
})
export class LogService {
  private API = environment.api + '/log';

  constructor(private httpClient: HttpClient) {}

  public count(): Observable<number> {
    return this.httpClient.get<number>(`${this.API}/count`);
  }

  public listLogs(first: number, size: number): Observable<Log[]> {
    const params = {
      first: first.toString(),
      numberOfElements: size.toString(),
    };
    return this.httpClient.get<Log[]>(`${this.API}/list`, { params });
  }
}
