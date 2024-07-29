import { HttpClient } from '@angular/common/http';
import { Component, Input, OnChanges } from '@angular/core';
import JSZip from 'jszip';
import { convertSizeToPrintable } from '../../../models/FileHeader';
import { getIconFor } from '../../../utils/file-icons';
import { MimeTypes } from '../../../utils/mime-types';

@Component({
  selector: 'app-zip-list',
  standalone: true,
  imports: [],
  templateUrl: './zip-list.component.html',
  styleUrl: './zip-list.component.css',
})
export class ZipListComponent implements OnChanges {
  @Input() src!: string;

  file!: File;
  files: { icon: string; name: string; date: string; size: string }[] = [];
  loaded = false;

  constructor(private http: HttpClient) {}

  ngOnChanges(): void {
    this.http.get(this.src, { responseType: 'blob' }).subscribe((response) => {
      this.file = new File([response], 'file.zip');
      this.readZipFile(this.file);
    });
  }

  async readZipFile(file: File) {
    const zip = new JSZip();
    const content = await zip.loadAsync(file);
    this.files = [];
    const filePromises: Promise<void>[] = [];
  
    content.forEach((_relativePath, zipEntry) => {
      const filePromise = zipEntry.async('uint8array').then((data) => {
        this.files.push({
          icon: getIconFor(MimeTypes.contentType(zipEntry.name) || ''),
          name: zipEntry.name,
          date: zipEntry.date.toLocaleDateString(),
          size: convertSizeToPrintable(data.length),
        });
      });
      filePromises.push(filePromise);
    });
  
    await Promise.all(filePromises);
    this.loaded = true;
  }
}
