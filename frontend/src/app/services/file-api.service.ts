import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { FileHeader, convertSizeToPrintable } from '../models/FileHeader';
import { environment } from '../../environments/environment.development';
import { Status } from '../enums/status';

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
  
  public getPages(page: number, size: number, filename: string, keywords?: string[], status?: Status[]): Observable<FileHeader[]> {
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    
    if (filename?.length > 0) {
      params = params.set('filename', filename);
    }
    if (keywords && keywords.length > 0) {
      params = params.set('keywords', keywords.join(','));
    }
    if (status && status.length > 0) {
      params = params.set('status', status.join(','));
    }
    
    return this.httpClient.get<FileHeader[]>(`${this.API}/files`, { params }).pipe(
      map((files: FileHeader[]) => {
        return files.map((file: FileHeader) => {
          file.printableSize = convertSizeToPrintable(file.size);
          return file;
        }, this);
      }
    ));
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

  public getKeywords(): Observable<string[]> {
    return this.httpClient.get<string[]>(`${this.API}/keywords`);
  }

  public delete(filename: string[]) {
    return this.httpClient.delete<void>(`${this.API}/delete`, {
      params: new HttpParams().set('filename', filename.join(','))
    });
  }
}
