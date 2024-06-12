import { Component, OnInit } from '@angular/core';
import { IconButtonComponent } from '../../shared/components/buttons/icon-button/icon-button.component';
import { FilecardComponent } from '../../shared/components/filecard/filecard.component';
import { PageSelectorComponent } from '../../shared/components/page-selector/page-selector.component';
import { TableComponent } from '../../shared/components/table/table.component';
import { IconTextButtonComponent } from '../../shared/components/buttons/icon-text-button/icon-text-button.component';
import { UploadDialogComponent } from '../upload-dialog/upload-dialog.component';
import { InputComponent } from '../../shared/components/form/input/input.component';
import { FileApiService } from '../../services/file-api.service';
import { FileHeader } from '../../models/FileHeader';
import { SelectComponent } from '../../shared/components/form/select/select.component';
import { SnackbarService } from '../../services/snackbar.service';

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
  files: FileHeader[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 16;
  numberOfElements!: number;

  filenameSearched: string = '';
  
  constructor(private api: FileApiService, private snackbar: SnackbarService) {
    this.api.getNumberOfElement().subscribe(n => {
      this.numberOfElements = n;
    });
  }
  
  ngOnInit(): void {
    this.refreshFileList();
  }
  
  onPageChange(pageNumber: number): void {
    this.currentPage = pageNumber
    this.refreshFileList();
  }
  
  onListClicked(): void {
    this.gridlayout = false;
  }

  onGridClicked(): void {
    this.gridlayout = true;
  }

  refreshFileList(): void {
    this.api.getPages(this.currentPage - 1, this.itemsPerPage, this.filenameSearched).subscribe(files => {
      this.files = files;
      for (let file of this.files) {
        if (file.thumbnailName != null) {
          this.api.getThumbnail(file.filename).subscribe(blob => {
            file.thumbnail = URL.createObjectURL(blob);
          });
        }
      }
    });
  }
}
