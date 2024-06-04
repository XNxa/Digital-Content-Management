import { Component, Input } from '@angular/core';

interface C {
  header: string,
  field: string
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

  constructor() { }

  ngOnInit(): void {
  }
}
