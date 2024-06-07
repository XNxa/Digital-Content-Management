import { Component, OnInit } from '@angular/core';
import { IconButtonComponent } from '../../shared/buttons/icon-button/icon-button.component';
import { FilecardComponent } from '../../shared/filecard/filecard.component';
import { PageSelectorComponent } from '../../shared/page-selector/page-selector.component';
import { TableComponent } from '../../shared/table/table.component';
import { IconTextButtonComponent } from '../../shared/buttons/icon-text-button/icon-text-button.component';
import { UploadDialogComponent } from '../upload-dialog/upload-dialog.component';
import { InputComponent } from '../../shared/form/input/input.component';
import { FileApiService } from '../../file-api.service';
import { FileHeader } from '../../interfaces/FileHeader';
import { SelectComponent } from '../../shared/form/select/select.component';

@Component({
  selector: 'app-file-view',
  standalone: true,
  imports: [
    IconButtonComponent,
    IconTextButtonComponent,
    FilecardComponent,
    PageSelectorComponent,
    TableComponent,
    UploadDialogComponent,
    InputComponent,
    SelectComponent
  ],
  templateUrl: './file-view.component.html',
  styleUrl: './file-view.component.css'
})
export class FileViewComponent implements OnInit {

  viewtitle = "Fichiers";

  gridlayout = true;

  isDialogOpen: boolean = false;
  
  files : FileHeader[] = [];

  constructor(private api : FileApiService) { }  

  ngOnInit(): void {
      this.api.getFiles().subscribe(files => {
        this.files = files;
      });
  }


  onPageChange($event: number) {
    throw new Error('Method not implemented.'); // TODO
  }

  onListClicked() {
    this.gridlayout = false;
    console.log("list clicked");
  }

  onGridClicked() {
    this.gridlayout = true;
  }

}
