import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FileHeader } from '../models/FileHeader';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class FileApiService {

  private API = environment.api; 

  constructor(private httpClient : HttpClient) { }

  public uploadFile(file: File, metadata: FileHeader): Observable<void> {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);
    formData.append('metadata', new Blob([JSON.stringify(metadata)], { type: 'application/json' }));

    return this.httpClient.post<void>(`${this.API}/upload`, formData);
  }

  public getNumberOfElement(): Observable<number> {
    return this.httpClient.get<number>(`${this.API}/count`);
  }
  
  public getPages(page: number, size: number): Observable<FileHeader[]> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.httpClient.get<FileHeader[]>(`${this.API}/files`, { params });
  }

  public getFileData(filename: string): Observable<Blob> {
    const params = new HttpParams().set('filename', filename);
    return this.httpClient.get<Blob>(`${this.API}/filedata`, {
      params,
      responseType: 'blob' as 'json'
    });
  }

  public getThumbnail(filename: string): Observable<Blob> {
    const params = new HttpParams().set('filename', filename);
    return this.httpClient.get<Blob>(`${this.API}/thumbnail`, {
      params,
      responseType: 'blob' as 'json'
    });
  }
}
