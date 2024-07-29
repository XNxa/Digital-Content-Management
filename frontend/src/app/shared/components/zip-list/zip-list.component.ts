import { HttpClient } from '@angular/common/http';
import { Component, Input, OnChanges } from '@angular/core';
import { unzip } from 'unzipit';
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
    const entries = await unzip(file);
    const names = Object.keys(entries.entries);
    this.files = await Promise.all(
      names.map((name) => {
        {
          const entry = entries.entries[name]; // Add 'as any' to bypass the type checking
          return {
            icon: getIconFor(MimeTypes.contentType(name) || ''),
            name,
            date: entry.lastModDate.toLocaleDateString(),
            size: convertSizeToPrintable(entry.size),
          };
        }
      }),
    );
    this.files.sort((a, b) => a.name.localeCompare(b.name));
    this.loaded = true;
  }
}
