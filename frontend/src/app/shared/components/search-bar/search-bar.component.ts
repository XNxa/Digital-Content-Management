import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Observable } from 'rxjs';
import { FileApiService } from '../../../services/file-api.service';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css',
})
export class SearchBarComponent<T> {
  @Input() searchMethod!: (value: string) => Observable<T>;

  open = false;
  hover = false;

  value: string = '';

  updateSearch() {
    if (this.value) {
      console.log('Searching for:', this.value);
      this.searchMethod(this.value).subscribe((r: T) =>
        console.log('Search result:', r),
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
