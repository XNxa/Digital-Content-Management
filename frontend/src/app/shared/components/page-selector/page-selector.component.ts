import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
} from '@angular/core';

@Component({
  selector: 'app-page-selector',
  standalone: true,
  imports: [],
  templateUrl: './page-selector.component.html',
  styleUrl: './page-selector.component.css',
})
export class PageSelectorComponent implements OnChanges {
  @Input() totalItems = 0;
  @Input() itemsPerPage = 10;
  @Input() page = 1;
  @Output() pageChange = new EventEmitter<number>();

  totalPages = 1;
  pages!: number[];

  ngOnChanges(): void {
    this.totalPages = Math.ceil((this.totalItems ?? 0) / this.itemsPerPage);
    this.updatePages();
  }

  updatePages(): void {
    if (this.totalPages <= 7) {
      this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
    } else {
      this.pages = this.generatePagesArray();
    }
  }

  generatePagesArray(): number[] {
    const pages = [];
    if (this.page <= 4) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i);
      }
    } else if (this.page > this.totalPages - 4) {
      for (let i = this.totalPages - 4; i <= this.totalPages; i++) {
        pages.push(i);
      }
    } else {
      pages.push(this.page - 1);
      pages.push(this.page);
      pages.push(this.page + 1);
      pages.push(this.page + 2);
    }
    return pages;
  }

  goToPage(page: number): void {
    this.page = page;
    this.updatePages();
    this.pageChange.emit(this.page);
  }

  previousPage(): void {
    if (this.page > 1) {
      this.page--;
      this.updatePages();
      this.pageChange.emit(this.page);
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages) {
      this.page++;
      this.updatePages();
      this.pageChange.emit(this.page);
    }
  }
}
