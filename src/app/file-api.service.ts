import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FileApiService {

  private API = "http://localhost:8080/api"

  constructor(private httpClient : HttpClient) { }

  public uploadFile(file : File) : Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.httpClient.post<string>(`${this.API}/upload`, formData);
  }
}
