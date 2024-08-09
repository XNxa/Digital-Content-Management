import { Component, OnInit } from '@angular/core';
import { TableComponent } from '../../shared/components/table/table.component';
import { LogService } from '../../services/log-api.service';
import { Log } from '../../models/Log';
import { PageSelectorComponent } from '../../shared/components/page-selector/page-selector.component';
import { InputComponent } from '../../shared/components/form/input/input.component';
import { SelectComponent } from '../../shared/components/form/select/select.component';
import { IconButtonComponent } from '../../shared/components/buttons/icon-button/icon-button.component';
import { DateInputComponent } from '../../shared/components/form/date-input/date-input.component';
import { FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-log-list',
  standalone: true,
  imports: [
    TableComponent,
    PageSelectorComponent,
    InputComponent,
    SelectComponent,
    IconButtonComponent,
    DateInputComponent,
  ],
  templateUrl: './log-list.component.html',
  styleUrl: './log-list.component.css',
})
export class LogListComponent implements OnInit {
  logs!: Log[];
  page = 1;
  totalLogs!: number;

  itemPerPage = 10;

  logDetailed: Log | undefined;

  search = new FormControl('');
  dateRange = new FormControl<[Date, Date] | null>(null);

  searchGroup = new FormGroup({
    search: this.search,
    dateRange: this.dateRange,
  }).valueChanges.subscribe(() => {
    this.refreshLogList();
  });

  constructor(private logService: LogService) {}

  ngOnInit(): void {
    this.refreshLogList();
  }

  changePage(newPage: number) {
    this.page = newPage;
    this.refreshLogList();
  }

  showDetails($event: number) {
    console.log('A');
    this.logDetailed = this.logs.at($event);
  }

  closeDetails() {
    this.logDetailed = undefined;
  }

  private refreshLogList() {
    this.logService
      .listLogs(
        this.page - 1,
        this.itemPerPage,
        this.search.value?.trim() || undefined,
        this.dateRange.value?.[0] ?? undefined,
        this.dateRange.value?.[1] ?? undefined,
      )
      .subscribe((response) => {
        this.logs = response.collection;
        this.totalLogs = response.size;
      });
  }
}
