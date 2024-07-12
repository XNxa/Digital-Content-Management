import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

interface C {
  header: string;
  field: string;
  image?: boolean;
}

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [],
  templateUrl: './table.component.html',
  styleUrl: './table.component.css',
})
export class TableComponent<Column extends C> implements OnInit {
  @Input() columns: Column[] = [];
  @Input() set data(data: object[]) {
    if (data && !this.validDataType(data)) {
      throw new Error('Invalid data type : ' + data);
    }
    this._data = data as Record<string, unknown>[];
  }
  _data: Record<string, unknown>[] = [];

  @Input() selected = new Set<number>();
  @Output() selectedChange = new EventEmitter<Set<number>>();

  @Output() rowClicked = new EventEmitter<number>();

  imageColumnPresent = false;
  imageColumn = {} as Column;
  columnsToDisplay = this.columns;

  ngOnInit(): void {
    this.imageColumnPresent = this.columns.some((c) => c.image);
    this.columnsToDisplay = this.columns.filter((c) => !c.image);
    if (this.imageColumnPresent)
      this.imageColumn = this.columns.find((c) => c.image)!;
  }

  validDataType(data: object[]): boolean {
    if (data.length === 0) return true;
    const dataKeys = Object.keys(data[0]);
    console.log(dataKeys);
    console.log(this.columns.map((c) => c.field));
    return this.columns.every((c) => dataKeys.includes(c.field));
  }

  onChecked(row: number): void {
    if (this.selected.has(row)) {
      this.selected.delete(row);
    } else {
      this.selected.add(row);
    }
    this.selectedChange.emit(this.selected);
  }

  isSelected(row: number): boolean {
    return this.selected.has(row);
  }

  onRowClicked(row: number) {
    this.rowClicked.emit(row);
  }

  stopPropagation($event: MouseEvent) {
    $event.stopPropagation();
  }
}
