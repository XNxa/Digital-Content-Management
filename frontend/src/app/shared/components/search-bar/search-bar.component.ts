import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { MimeTypes } from '../../../utils/mime-types';
import { getIconFor } from '../../../utils/file-icons';

interface RequiredFields {
  filename: string,
  type: string,
  printableSize: string,
  thumbnail: string | undefined,
}

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css',
})
export class SearchBarComponent<T extends RequiredFields> {
  @Input() searchMethod!: (value: string) => Observable<T[]>;

  open = false;
  hover = false;

  value: string = '';

  results: T[] = [];

  toExtentsion = (type: string) => (MimeTypes.extension(type) || '').toUpperCase();
  getIcon = getIconFor

  updateSearch() {
    if (this.value) {
      this.searchMethod(this.value).subscribe((r: T[]) =>
        this.results = r
      );
    }
  }

  setHover(value: boolean) {
    if (this.open) {
      return;
    }
    this.hover = value;
  }

  enterFocus() {
    this.open = true;
  }

  exitFocus() {
    this.open = false;
    this.hover = false;
    this.value = '';
  }

  toggleFocus() {
    if (this.open) {
      this.exitFocus();
    } else {
      this.enterFocus();
      this.hover = true;
    }
  }
}
