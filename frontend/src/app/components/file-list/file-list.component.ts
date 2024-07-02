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
import { SnackbarService } from '../../shared/components/snackbar/snackbar.service';
import { DropdownCheckboxComponent } from '../../shared/components/form/dropdown-checkbox/dropdown-checkbox.component';
import { Status } from '../../enums/status';
import { lastValueFrom } from 'rxjs';
import { FileDetailsComponent } from '../file-details/file-details.component';
import { ActivatedRoute, Router } from '@angular/router';
import { getCategoryFromPath, getNameFromPath } from '../../models/Tabs';
import { FormControl } from '@angular/forms';

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
  filenameSearched = new FormControl('');;

  /** Array of keywords being searched. 
   * Binded to the search field */
  keywordsSearched: string[] = [];

  /** Array of status strings being searched. 
   * Binded to the search field */
  statusSearched: string[] = [];

  /** Files currently selected */
  selectedFiles: Set<number> = new Set<number>();

  /** Flag indicating whether the file detail view is open or not. */
  fileDetailOpen = false;

  /** Index of the file to open in the file detail view. */
  indexToOpen: number = 0;

  /** Button state for the multi-select button. */
  buttonMultiSelect: 'Empty' | 'Full' = 'Empty';

  /** Folder selected */
  folder!: string;
  displayableFolder!: string;

  /** Type of media selected */
  typeFolder!: string;
  displayableTypeFolder!: string;



  constructor(private api: FileApiService, private snackbar: SnackbarService, private route: ActivatedRoute, private router: Router) {
    this.api.getNumberOfElement().subscribe(n => {
      this.numberOfElements = n;
    });
    this.api.getKeywords().subscribe(keywords => {
      this.keywords = keywords;
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const currentUrl = this.router.url;
      const urlSegments = currentUrl.split('/');
      this.folder = urlSegments.slice(0, urlSegments.length - 1).join('');
      this.typeFolder = params['type'];
      this.displayableFolder = getNameFromPath(this.folder);
      this.displayableTypeFolder = getNameFromPath(this.typeFolder);
      this.refreshFileList();
    });
  }

  onCloseDialog() {
    this.isDialogOpen = false;

    setTimeout(() => {
      this.refreshFileList();
    }, 1000);
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
    this.api.getPages(
      this.currentPage - 1,
      this.itemsPerPage,
      this.folder + '/' + this.filenameSearched.value,
      getCategoryFromPath(this.typeFolder),
      this.keywordsSearched,
      this.statusSearched.map(s => Status.fromString(s))
    ).subscribe(files => {
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
    this.api.delete([...this.selectedFiles].map(index => this.files[index].filename)).subscribe({
      next: () => {
        this.files = this.files.filter((_, index) => !this.selectedFiles.has(index));
        this.selectedFiles.clear();
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
    if (this.selectedFiles.has(index)) {
      this.selectedFiles.delete(index);
    } else {
      this.selectedFiles.add(index);
      if (this.selectedFiles.size == this.files.length) {
        this.buttonMultiSelect = 'Full';
      }
    }
  }

  onMenuButtonClicked(buttonClicked: string) {
    switch (buttonClicked) {
      case 'Dupliquer':
        const filenames = [...this.selectedFiles].map(index => this.files[index].filename);
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
      case 'Partager par e-mail':
        const selectedFiles = [...this.selectedFiles].map(index => this.files[index]);
        const linkPromises = selectedFiles.map(file => lastValueFrom(this.api.getLink(file.filename)));

        Promise.all(linkPromises).then(links => {
          const mailtoLinks = links.map(link => `- ${encodeURIComponent(link)}`).join('%0A%0A');
          const mailtoLink = `mailto:?subject=Partage de fichier&body=Bonjour,%0A%0AVeuillez trouver ci-joint les liens vers les fichiers :%0A${mailtoLinks}`;
          window.location.href = mailtoLink;
        });
        break;
    }
  }

  fileClicked(index: number): void {
    this.indexToOpen = index;
    this.fileDetailOpen = true;
  }

  closeFileDetail(): void {
    this.fileDetailOpen = false;
  }

  onSelectClick(): void {
    this.buttonMultiSelect = this.buttonMultiSelect == 'Empty' ? 'Full' : 'Empty';
    if (this.buttonMultiSelect == 'Empty') {
      this.selectedFiles.clear();
    } else {
      this.selectedFiles = new Set(this.files.map((_, index) => index));
    }
  }

  fileSelectedList($event: Set<number>) {
    this.selectedFiles = $event;
    if (this.selectedFiles.size == this.files.length) {
      this.buttonMultiSelect = 'Full';
    } else {
      this.buttonMultiSelect = 'Empty';
    }
  }

  nextFile() {
    this.indexToOpen = (this.indexToOpen + 1) % this.files.length;
    this.refreshFileList();
  }

  previousFile() {
    this.indexToOpen = (this.indexToOpen - 1 + this.files.length) % this.files.length;
  }
}
