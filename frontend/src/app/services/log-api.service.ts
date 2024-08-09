import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Log } from '../models/Log';
import { dateToString } from '../utils/date-util';
import { PaginatedResponse } from '../models/PaginatedResponse';

@Injectable({
  providedIn: 'root',
})
export class LogService {
  private API = environment.api + '/log';

  constructor(private httpClient: HttpClient) {}

  public listLogs(
    first: number,
    size: number,
    searchQuery?: string,
    dateFrom?: Date,
    dateTo?: Date,
  ): Observable<PaginatedResponse<Log>> {
    let params = new HttpParams()
    .set('page', first)
    .set('size', size)
    if (searchQuery) {
      params = params.set('search', searchQuery);
    }
    if (dateFrom) {
      params = params.set('dateFrom', dateToString(dateFrom)!);
    }
    if (dateTo) {
      params = params.set('dateTo', dateToString(dateTo)!);
    }
  
    return this.httpClient.get<PaginatedResponse<Log>>(`${this.API}/list`, { params });
  }

  public logLogin(): Observable<void> {
    return this.httpClient.get<void>(`${this.API}/connected`);
  }

  public logLogout(): Observable<void> {
    return this.httpClient.get<void>(`${this.API}/disconnected`);
  }
}
