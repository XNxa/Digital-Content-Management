import { Component, Input } from '@angular/core';

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
}
