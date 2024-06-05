import { Component } from '@angular/core';
import { IconButtonComponent } from '../../shared/buttons/icon-button/icon-button.component';
import { FilecardComponent } from '../../shared/filecard/filecard.component';
import { PageSelectorComponent } from '../../shared/page-selector/page-selector.component';
import { TableComponent } from '../../shared/table/table.component';

@Component({
  selector: 'app-file-view',
  standalone: true,
  imports: [IconButtonComponent, FilecardComponent, PageSelectorComponent, TableComponent],
  templateUrl: './file-view.component.html',
  styleUrl: './file-view.component.css'
})
export class FileViewComponent {
  viewtitle = "Fichiers";

  gridlayout = true;

  files = [{ name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "deux", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "trois", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "quatre", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }, { name: "un", type: "png", date: "25/12/1999", size: "1 Mo" }]

  onPageChange($event: number) {
    throw new Error('Method not implemented.'); // TODO
  }

  onListClicked() {
    this.gridlayout = false; 
  }

  onGridClicked() {
    this.gridlayout = true;
  }

}
