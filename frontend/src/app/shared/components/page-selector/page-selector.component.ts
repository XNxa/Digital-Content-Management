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
  @Output() pageChange = new EventEmitter<number>();

  currentPage = 1;
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
    if (this.currentPage <= 4) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i);
      }
    } else if (this.currentPage > this.totalPages - 4) {
      for (let i = this.totalPages - 4; i <= this.totalPages; i++) {
        pages.push(i);
      }
    } else {
      pages.push(this.currentPage - 1);
      pages.push(this.currentPage);
      pages.push(this.currentPage + 1);
      pages.push(this.currentPage + 2);
    }
    return pages;
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.updatePages();
    this.pageChange.emit(this.currentPage);
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.updatePages();
      this.pageChange.emit(this.currentPage);
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.updatePages();
      this.pageChange.emit(this.currentPage);
    }
  }
}
