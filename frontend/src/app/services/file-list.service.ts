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

  private last_request: {
    currentPage: number;
    itemsPerPage: number;
    folder: string;
    typeFolder: string;
    filename: string;
    keywords?: string[];
    status?: Status[];
    version?: string;
    type?: string[];
    dateFrom?: Date;
    dateTo?: Date;
  } | undefined;

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

    this.last_request = {
      currentPage,
      itemsPerPage,
      folder,
      typeFolder,
      filename,
      keywords,
      status,
      version,
      type,
      dateFrom,
      dateTo,
    };
  }

  next(id: string): string {
    const files = this.filesSubject.getValue();
    const index = files.findIndex((f) => f.id == id);
    console.log(files.length)
    if (index == -1) {
      return files[0].id;
    }
    return files[(index + 1) % files.length].id;
  }

  previous(id: string): string | null {
    const files = this.filesSubject.getValue();
    const index = files.findIndex((f) => f.id == id);
    if (index == -1) {
      return (files.length == 0) ? null : files[0].id
    }
    return files[(index - 1 + files.length) % files.length].id;
  }

  refresh() {
    if (this.last_request != undefined) {
      this.fetchFiles(
        this.last_request.currentPage,
        this.last_request.itemsPerPage,
        this.last_request.folder,
        this.last_request.typeFolder,
        this.last_request.filename,
        this.last_request.keywords,
        this.last_request.status,
        this.last_request.version,
        this.last_request.type,
        this.last_request.dateFrom,
        this.last_request.dateTo
      );
    }
  }

  remove(id: string) {
    this.filesSubject.next(this.filesSubject.getValue().filter(f => f.id != id));
    this.refresh()
  }

  private getThumbnail(file: FileHeader): void {
    if (file.thumbnailName) {
      this.api.getThumbnail(file.folder, file.filename).subscribe((url) => {
        file.thumbnail = url;
      });
    }
  }
}
