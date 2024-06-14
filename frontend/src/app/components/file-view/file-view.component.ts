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

  /** The title of the file view. */
  readonly viewtitle: string = "Fichiers";

  /** The number of items to display per page. */
  readonly itemsPerPage: number = 16;
  
  /** Array of status strings. */
  readonly status : string[] = Status.getStringList();

  /** Flag indicating whether to display files in grid layout or list layout. */
  gridlayout: boolean = true;

  /** Flag indicating whether the upload dialog is open or not. */
  isDialogOpen: boolean = false;

  /** Array of file headers to display. */
  files: FileHeader[] = [];

  /** The current page number. */
  currentPage: number = 1;

  /** The total number of elements. */
  numberOfElements!: number;

  /** Array currently defined keywords. */
  keywords!: string[];

  /** The filename being searched. 
   * Binded to the search field */
  filenameSearched: string = '';

  /** Array of keywords being searched. 
   * Binded to the search field */
  keywordsSearched: string[] = [];

  /** Array of status strings being searched. 
   * Binded to the search field */
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

  /**
   * Event handler for page change.
   * @param pageNumber The new page number.
   */
  onPageChange(pageNumber: number): void {
    this.currentPage = pageNumber
    this.refreshFileList();
  }

  /** Event handler for list view button click. */
  onListClicked(): void {
    this.gridlayout = false;
  }

  /** Event handler for grid view button click. */
  onGridClicked(): void {
    this.gridlayout = true;
  }

  /** Fetch file list from the server. Get files of current page only. */
  private refreshFileList(): void {
    // Get the files for the current page and search criteria
    this.api.getPages(this.currentPage - 1, this.itemsPerPage, this.filenameSearched, this.keywordsSearched, this.statusSearched.map(s => Status.fromString(s))).subscribe(files => {
      this.files = files;
      for (let file of this.files) {
        if (file.thumbnailName != null) {
          // Get the thumbnail for each file
          this.api.getThumbnail(file.filename).subscribe(blob => {
            file.thumbnail = URL.createObjectURL(blob);
          });
        }
      }
    });

    // Refresh the list of keywords
    this.api.getKeywords().subscribe(keywords => {
      this.keywords = keywords;
    });
  }
}
