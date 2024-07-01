import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { FileHeader, convertSizeToPrintable } from '../models/FileHeader';
import { environment } from '../../environments/environment.development';
import { Status } from '../enums/status';
import { FileCategory } from '../models/Tabs';

@Injectable({
  providedIn: 'root'
})
export class FileApiService {

  private API = environment.api + '/file';

  constructor(private httpClient: HttpClient) { }

  public uploadFile(file: File, metadata: FileHeader): Observable<void> {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);
    formData.append('metadata', new Blob([JSON.stringify(metadata)], { type: 'application/json' }));

    return this.httpClient.post<void>(`${this.API}/upload`, formData);
  }

  public getNumberOfElement(): Observable<number> {
    return this.httpClient.get<number>(`${this.API}/count`);
  }

  public getPages(page: number, size: number, filename: string, type: FileCategory, keywords?: string[], status?: Status[]): Observable<FileHeader[]> {
    const filter = {
      page: page.toString(),
      size: size.toString(),
      filename: filename,
      keywords: (keywords || []),
      status: (status || []),
      category: type,
    };
    return this.httpClient.post<FileHeader[]>(`${this.API}/files`, filter).pipe(
      map((files: FileHeader[]) => {
        return files.map((file: FileHeader) => {
          file.printableFilename = file.filename.split('/').pop() || '';
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

  public delete(filename: string[]): Observable<void> {
    return this.httpClient.delete<void>(`${this.API}/delete`, {
      params: new HttpParams().set('filename', filename.join(','))
    });
  }

  public getLink(filename: string): Observable<string> {
    return this.httpClient.get<string>(`${this.API}/link`, {
      params: new HttpParams().set('filename', filename),
      responseType: 'text' as 'json'
    });
  }

  public duplicate(filename: string): Observable<void> {
    return this.httpClient.post<void>(`${this.API}/duplicate`, null, {
      params: new HttpParams().set('filename', filename)
    });
  }

  public update(filename: string, metadata: FileHeader): Observable<void> {
    return this.httpClient.put<void>(`${this.API}/update`, metadata, {
      params: new HttpParams().set('filename', filename)
    });
  }
}
