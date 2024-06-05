import { Component } from '@angular/core';
import { IconButtonComponent } from '../../shared/buttons/icon-button/icon-button.component';
import { FilecardComponent } from '../../shared/filecard/filecard.component';
import { PageSelectorComponent } from '../../shared/page-selector/page-selector.component';
import { TableComponent } from '../../shared/table/table.component';
import { IconTextButtonComponent } from '../../shared/buttons/icon-text-button/icon-text-button.component';
import { UploadDialogComponent } from '../upload-dialog/upload-dialog.component';
import { FileApiService } from '../../file-api.service';

@Component({
  selector: 'app-file-view',
  standalone: true,
  imports: [IconButtonComponent, IconTextButtonComponent, FilecardComponent, PageSelectorComponent, TableComponent, UploadDialogComponent],
  templateUrl: './file-view.component.html',
  styleUrl: './file-view.component.css'
})
export class FileViewComponent {

  viewtitle = "Fichiers";
  
  gridlayout = true;
  
  isDialogOpen: boolean = false;

  constructor(private api : FileApiService) { }
  
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

  upload(file: File) {
    this.api.uploadFile(file).subscribe({
      next(value) {
          console.log(value)
      },
      error(e) {
        console.log(e)
      }
    })    
  }
}
