import { Component, OnInit } from '@angular/core';
import { TableComponent } from '../../shared/components/table/table.component';
import { LogService } from '../../services/log-api.service';
import { Log } from '../../models/Log';
import { PageSelectorComponent } from '../../shared/components/page-selector/page-selector.component';

@Component({
  selector: 'app-log-list',
  standalone: true,
  imports: [TableComponent, PageSelectorComponent],
  templateUrl: './log-list.component.html',
  styleUrl: './log-list.component.css',
})
export class LogListComponent implements OnInit {
  logs!: Log[];
  page = 1;
  totalLogs!: number;

  itemPerPage = 10;

  constructor(private logService: LogService) {}

  ngOnInit(): void {
    this.refreshLogList();
  }

  changePage(newPage: number) {
    this.page = newPage;
    this.refreshLogList();
  }

  private refreshLogList() {
    this.logService.listLogs(this.page - 1, this.itemPerPage).subscribe((logs) => {
      this.logs = logs;
    });
    this.logService.count().subscribe((n) => {
      this.totalLogs = n;
    });
  }
}
