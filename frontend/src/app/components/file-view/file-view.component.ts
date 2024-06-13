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
import { DropdownCheckboxComponent } from '../../shared/components/form/dropdown-checkbox/dropdown-checkbox.component';
import { Status } from '../../enums/status';

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
    SelectComponent,
    DropdownCheckboxComponent
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
  keywords!: string[];
  status = Status.getStringList();

  filenameSearched: string = '';
  keywordsSearched: string[] = [];
  statusSearched: string[] = [];
  
  constructor(private api: FileApiService, private snackbar: SnackbarService) {
    this.api.getNumberOfElement().subscribe(n => {
      this.numberOfElements = n;
    });
    this.api.getKeywords().subscribe(keywords => {
      this.keywords = keywords;
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
    this.api.getPages(this.currentPage - 1, this.itemsPerPage, this.filenameSearched, this.keywordsSearched, this.statusSearched.map(s => Status.fromString(s))).subscribe(files => {
      this.files = files;
      for (let file of this.files) {
        if (file.thumbnailName != null) {
          this.api.getThumbnail(file.filename).subscribe(blob => {
            file.thumbnail = URL.createObjectURL(blob);
          });
        }
      }
    });
    this.api.getKeywords().subscribe(keywords => {
      this.keywords = keywords;
    });
  }
}
