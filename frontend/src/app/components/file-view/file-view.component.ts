import { Component, OnInit } from '@angular/core';
import { IconButtonComponent } from '../../shared/buttons/icon-button/icon-button.component';
import { FilecardComponent } from '../../shared/filecard/filecard.component';
import { PageSelectorComponent } from '../../shared/page-selector/page-selector.component';
import { TableComponent } from '../../shared/table/table.component';
import { IconTextButtonComponent } from '../../shared/buttons/icon-text-button/icon-text-button.component';
import { UploadDialogComponent } from '../upload-dialog/upload-dialog.component';
import { InputComponent } from '../../shared/form/input/input.component';
import { FileApiService } from '../../file-api.service';
import { FileHeader } from '../../models/FileHeader';
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
  files: FileHeader[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 10;
  numberOfElements!: number;

  constructor(private api: FileApiService) {
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
    this.api.getPages(this.currentPage - 1, this.itemsPerPage).subscribe(files => {
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
