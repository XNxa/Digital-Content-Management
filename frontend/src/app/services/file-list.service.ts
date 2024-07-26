import { Injectable } from '@angular/core';
import { FileHeader } from '../models/FileHeader';
import { BehaviorSubject, Observable } from 'rxjs';
import { FileApiService } from './file-api.service';
import { Status } from '../enums/status';

@Injectable({
  providedIn: 'root',
})
export class FileListService {
  private filesSubject = new BehaviorSubject<FileHeader[]>([]);
  files$: Observable<FileHeader[]> = this.filesSubject.asObservable();

  private numberOfElementsSubject = new BehaviorSubject<number>(0);
  numberOfElements$ = this.numberOfElementsSubject.asObservable();

  private keywordsSubject = new BehaviorSubject<string[]>([]);
  keywords$ = this.keywordsSubject.asObservable();

  private typesSubject = new BehaviorSubject<string[]>([]);
  types$ = this.typesSubject.asObservable();

  constructor(private api: FileApiService) {}

  fetchFiles(
    currentPage: number,
    itemsPerPage: number,
    folder: string,
    typeFolder: string,
    filename: string,
    keywords?: string[],
    status?: Status[],
    version?: string,
    type?: string[],
    dateFrom?: Date,
    dateTo?: Date,
  ): void {
    this.api
      .getPages(
        currentPage - 1,
        itemsPerPage,
        folder + '/' + typeFolder,
        filename,
        keywords,
        status,
        version,
        type,
        dateFrom,
        dateTo,
      )
      .subscribe((files) => {
        this.filesSubject.next(files);
        for (const file of files) {
          this.getThumbnail(file);
        }
      });

    this.api
      .getNumberOfElement(
        folder + '/' + typeFolder,
        filename,
        keywords,
        status,
        version,
        type,
        dateFrom,
        dateTo,
      )
      .subscribe((n) => {
        this.numberOfElementsSubject.next(n);
      });

    this.api.getKeywords().subscribe((k) => {
      this.keywordsSubject.next(k);
    });

    this.api.getTypes(folder + '/' + typeFolder).subscribe((t) => {
      this.typesSubject.next(t);
    });
  }

  next(id: string): string {
    const files = this.filesSubject.getValue();
    const index = files.findIndex(f => f.id == id)
    if (index ==  -1) {
      return files[0].id;
    } 
    return files[index + 1 % files.length].id;
  }

  previous(id: string): string {
    const files = this.filesSubject.getValue();
    const index = files.findIndex(f => f.id == id)
    if (index ==  -1) {
      return files[0].id;
    } 
    return files[(index - 1 + files.length) % files.length].id;
  }

  private getThumbnail(file: FileHeader): void {
    if (file.thumbnailName) {
      this.api.getThumbnail(file.folder, file.filename).subscribe((url) => {
        file.thumbnail = url;
      });
    }
  }
}
