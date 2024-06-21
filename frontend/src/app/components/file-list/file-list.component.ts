import { Component, OnInit } from '@angular/core';
import { IconButtonComponent } from '../../shared/components/buttons/icon-button/icon-button.component';
import { FilecardComponent } from '../../shared/components/filecard/filecard.component';
import { PageSelectorComponent } from '../../shared/components/page-selector/page-selector.component';
import { TableComponent } from '../../shared/components/table/table.component';
import { IconTextButtonComponent } from '../../shared/components/buttons/icon-text-button/icon-text-button.component';
import { IconMenuButtonComponent } from '../../shared/components/buttons/icon-menu-button/icon-menu-button.component';
import { UploadDialogComponent } from '../upload-dialog/upload-dialog.component';
import { InputComponent } from '../../shared/components/form/input/input.component';
import { FileApiService } from '../../services/file-api.service';
import { FileHeader } from '../../models/FileHeader';
import { SelectComponent } from '../../shared/components/form/select/select.component';
import { SnackbarService } from '../../services/snackbar.service';
import { DropdownCheckboxComponent } from '../../shared/components/form/dropdown-checkbox/dropdown-checkbox.component';
import { Status } from '../../enums/status';
import { lastValueFrom } from 'rxjs';
import { FileDetailsComponent } from '../file-details/file-details.component';

@Component({
  selector: 'app-file-list',
  standalone: true,
  imports: [
    IconButtonComponent,
    IconTextButtonComponent,
    IconMenuButtonComponent,
    FilecardComponent,
    PageSelectorComponent,
    TableComponent,
    UploadDialogComponent,
    InputComponent,
    SelectComponent,
    DropdownCheckboxComponent,
    FileDetailsComponent
  ],
  templateUrl: './file-list.component.html',
  styleUrl: './file-list.component.css'
})
export class FileListComponent implements OnInit {
  
  /** The title of the file view. */
  readonly viewtitle: string = "Fichiers";

  /** The number of items to display per page. */
  readonly itemsPerPage: number = 16;
  
  /** Array of status strings. */
  readonly status: string[] = Status.getStringList();
  
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

  /** Files currently selected */
  selectedFiles: number[] = [];
  
  /** Flag indicating whether the file detail view is open or not. */
  fileDetailOpen = false;
  
  /** Index of the file to open in the file detail view. */
  indexToOpen: number = 0;
  
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
  refreshFileList(): void {
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

  onDeleteClicked() {
    this.api.delete(this.selectedFiles.map(index => this.files[index].filename)).subscribe({
      next: () => {
        this.files = this.files.filter((_, index) => !this.selectedFiles.includes(index));
        this.selectedFiles = [];
        this.snackbar.show('Fichiers supprimés avec succès');
        this.api.getNumberOfElement().subscribe(n => {
          this.numberOfElements = n;
        });
        this.api.getKeywords().subscribe(keywords => {
          this.keywords = keywords;
        });
      },
      error: () => {
        this.snackbar.show('Erreur lors de la suppression des fichiers');
      }
    });
  }

  fileSelected(index: number) {
    if (this.selectedFiles.includes(index)) {
      this.selectedFiles = this.selectedFiles.filter(i => i != index);
    } else {
      this.selectedFiles.push(index);
    }
  }

  onMenuButtonClicked(buttonClicked: string) {
    switch (buttonClicked) {
      case 'Dupliquer':
        const filenames = this.selectedFiles.map(index => this.files[index].filename);
        filenames.forEach(filename => {
          this.api.duplicate(filename).subscribe({
            next: () => {
              this.snackbar.show('Fichier dupliqué avec succès');
            },
            error: () => {
              this.snackbar.show('Erreur lors de la duplication du fichier');
            }
          });
        });
        break;
      case 'Télécharger':
        for (let index of this.selectedFiles) {
          this.api.getFileData(this.files[index].filename).subscribe({
            next: blob => {
              const url = URL.createObjectURL(blob);
              const a = document.createElement('a');
              a.href = url;
              a.download = this.files[index].filename;
              a.click();
              URL.revokeObjectURL(url);
            },
            error: () => {
              this.snackbar.show('Erreur lors du téléchargement');
            }
          });
        }
        break;
      case 'Copier le lien':
        this.api.getLink(this.files[this.selectedFiles[0]].filename).subscribe(link => {
          navigator.clipboard.writeText(link).then(() => {
            this.snackbar.show('Lien copié dans le presse-papier');
          });
        });
        break;
        case 'Partager par e-mail':
        const selectedFiles = this.selectedFiles.map(index => this.files[index]);
        const linkPromises = selectedFiles.map(file => lastValueFrom(this.api.getLink(file.filename)));

        Promise.all(linkPromises).then(links => {
          const mailtoLinks = links.map(link => `- ${encodeURIComponent(link)}`).join('%0A%0A');
          const mailtoLink = `mailto:?subject=Partage de fichier&body=Bonjour,%0A%0AVeuillez trouver ci-joint les liens vers les fichiers :%0A${mailtoLinks}`;
          window.location.href = mailtoLink;
        });
        break;
    }
  }

  fileClicked(index: number) {
    this.indexToOpen = index;
    this.fileDetailOpen = true;
  }
  
  closeFileDetail() {
    this.fileDetailOpen = false;
  }
}
