import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, ObservedValueOf, map } from 'rxjs';
import { FileHeader, convertSizeToPrintable } from '../models/FileHeader';
import { environment } from '../../environments/environment.development';
import { Status } from '../enums/status';

@Injectable({
  providedIn: 'root',
})
export class FileApiService {
  private API = environment.api + '/file';

  constructor(private httpClient: HttpClient) {}

  public uploadFile(file: File, metadata: FileHeader): Observable<void> {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);
    formData.append(
      'metadata',
      new Blob([JSON.stringify(metadata)], { type: 'application/json' }),
    );

    return this.httpClient.post<void>(`${this.API}/upload`, formData);
  }

  public getNumberOfElement(
    folder: string,
    filename: string,
    keywords?: string[],
    status?: Status[],
    version?: string,
    type?: string[],
    dateFrom?: Date,
    dateTo?: Date,
  ): Observable<number> {
    const filter = {
      page: 0,
      size: 0,
      folder: folder,
      filename: filename,
      keywords: keywords || [],
      status: status || [],
      version: version,
      type: type,
      dateFrom: dateToString(dateFrom),
      dateTo: dateToString(dateTo),
    };
    return this.httpClient.post<number>(`${this.API}/count`, filter);
  }

  public getPages(
    page: number,
    size: number,
    folder: string,
    filename: string,
    keywords?: string[],
    status?: Status[],
    version?: string,
    type?: string[],
    dateFrom?: Date,
    dateTo?: Date,
  ): Observable<FileHeader[]> {
    const filter = {
      page: page,
      size: size,
      folder: folder,
      filename: filename,
      keywords: keywords || [],
      status: status || [],
      version: version,
      type: type,
      dateFrom: dateToString(dateFrom),
      dateTo: dateToString(dateTo),
    };
    return this.httpClient.post<FileHeader[]>(`${this.API}/files`, filter).pipe(
      map((files: FileHeader[]) => {
        return files.map((file: FileHeader) => {
          file.printableSize = convertSizeToPrintable(file.size);
          return file;
        });
      }),
    );
  }

  public getFileData(folder: string, filename: string): Observable<Blob> {
    const params = new HttpParams()
      .set('folder', folder)
      .set('filename', filename);
    return this.httpClient.get<Blob>(`${this.API}/filedata`, {
      params,
      responseType: 'blob' as 'json',
    });
  }

  public getThumbnail(folder: string, filename: string): Observable<Blob> {
    const params = new HttpParams()
      .set('folder', folder)
      .set('filename', filename);
    return this.httpClient.get<Blob>(`${this.API}/thumbnail`, {
      params,
      responseType: 'blob' as 'json',
    });
  }

  public getKeywords(): Observable<string[]> {
    return this.httpClient.get<string[]>(`${this.API}/keywords`);
  }

  public delete(folder: string, filename: string): Observable<void> {
    const params = new HttpParams()
      .set('folder', folder)
      .set('filename', filename);
    return this.httpClient.delete<void>(`${this.API}/delete`, { params });
  }

  public getLink(folder: string, filename: string): Observable<string> {
    const params = new HttpParams()
      .set('folder', folder)
      .set('filename', filename);
    return this.httpClient.get<string>(`${this.API}/link`, {
      params,
      responseType: 'text' as 'json',
    });
  }

  public duplicate(folder: string, filename: string): Observable<void> {
    const params = new HttpParams()
      .set('folder', folder)
      .set('filename', filename);
    return this.httpClient.post<void>(`${this.API}/duplicate`, null, {
      params,
    });
  }

  public update(
    folder: string,
    filename: string,
    metadata: FileHeader,
  ): Observable<void> {
    metadata.thumbnail = undefined;
    const params = new HttpParams()
      .set('folder', folder)
      .set('filename', filename);
    return this.httpClient.put<void>(`${this.API}/update`, metadata, {
      params,
    });
  }

  public getTypes(folder: string): Observable<string[]> {
    const params = new HttpParams().set('folder', folder).set('filename', '');
    return this.httpClient.get<string[]>(`${this.API}/types`, { params });
  }

  public getNewFiles(): Observable<number[]> {
    return this.httpClient.get<number[]>(`${this.API}/new-stats`);
  }

  public getStatuses(): Observable<number[]> {
    return this.httpClient.get<number[]>(`${this.API}/status-stats`);
  }

  public search(query: string): Observable<FileHeader[]> {
    const params = new HttpParams().set('query', query);
    return this.httpClient
      .get<FileHeader[]>(`${this.API}/search`, { params })
      .pipe(
        map((files: FileHeader[]) => {
          return files.map((file: FileHeader) => {
            file.printableSize = convertSizeToPrintable(file.size);
            if (file.thumbnail) file.thumbnail = thumbnailToUrl(file.thumbnail);
            return file;
          });
        }),
      );
  }
}

function dateToString(date: Date | undefined): string | undefined {
  if (date == undefined) {
    return undefined;
  }

  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // Months are 0-based in JavaScript, so add 1
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`;
}

function thumbnailToUrl(base64Str: string): string {
  const byteCharacters = atob(base64Str);
  const byteNumbers = new Array(byteCharacters.length);
  for (let i = 0; i < byteCharacters.length; i++) {
    byteNumbers[i] = byteCharacters.charCodeAt(i);
  }
  const byteArray = new Uint8Array(byteNumbers);
  return URL.createObjectURL(new Blob([byteArray], { type: 'image/png' }));
}
