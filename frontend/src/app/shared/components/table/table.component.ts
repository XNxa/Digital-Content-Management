import { Component, EventEmitter, Input, Output } from '@angular/core';

interface C {
  header: string,
  field: string,
  image?: boolean
}

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [],
  templateUrl: './table.component.html',
  styleUrl: './table.component.css'
})
export class TableComponent<Column extends C> {
  @Input() columns: Column[] = [];
  @Input() data: any[] = [];

  @Input() selected: Set<number> = new Set();
  @Output() selectedChange = new EventEmitter<Set<number>>();

  @Output() rowClicked = new EventEmitter<number>();

  imageColumnPresent = false;
  imageColumn = {} as Column;
  columnsToDisplay = this.columns;

  constructor() { }

  ngOnInit(): void {
    this.imageColumnPresent = this.columns.some(c => c.image);
    this.columnsToDisplay = this.columns.filter(c => !c.image);
    if (this.imageColumnPresent)
      this.imageColumn = this.columns.find(c => c.image)!;
  }

  onChecked(row: number): void {
    this.selected.has(row) ? this.selected.delete(row) : this.selected.add(row);
    this.selectedChange.emit(this.selected);
  }

  isSelected(row: number): boolean {
    return this.selected.has(row);
  }

  onRowClicked(row: number) {
    this.rowClicked.emit(row);
  }
}
