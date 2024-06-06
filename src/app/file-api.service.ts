import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Status } from './interfaces/status';
import { Version } from './interfaces/version';
import { FileHeader } from './interfaces/FileHeader';

@Injectable({
  providedIn: 'root'
})
export class FileApiService {

  private API = "http://localhost:8080/api"

  constructor(private httpClient : HttpClient) { }

  public uploadFile(file : File, description : string, version: Version, status : Status, keywords : string[]) : Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('description', description);
    formData.append('version', version);
    formData.append('status', status.toUpperCase().replace(' ', '_').replace('É', 'E'));
    formData.append('keywords', keywords.toString());
    return this.httpClient.post<string>(`${this.API}/upload`, formData);
  }

  public getFiles() : Observable<FileHeader[]> {
    return this.httpClient.get<FileHeader[]>(`${this.API}/files`);
  }
}
