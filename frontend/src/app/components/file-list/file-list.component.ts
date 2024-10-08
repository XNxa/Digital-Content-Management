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
import { DropdownCheckboxComponent } from '../../shared/components/form/dropdown-checkbox/dropdown-checkbox.component';
import { Status } from '../../enums/status';
import { lastValueFrom } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { getNameFromPath } from '../../models/Tabs';
import { FormControl } from '@angular/forms';
import { PermissionDirective } from '../../shared/directives/permission.directive';
import { DateInputComponent } from '../../shared/components/form/date-input/date-input.component';
import { FileListService } from '../../services/file-list.service';
import { ConfirmationDialogService } from '../../shared/components/confirmation-dialog/confirmation-dialog.service';
import { SearchBarComponent } from '../../shared/components/search-bar/search-bar.component';

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
    PermissionDirective,
    DateInputComponent,
    SearchBarComponent,
  ],
  templateUrl: './file-list.component.html',
  styleUrl: './file-list.component.css',
})
export class FileListComponent implements OnInit {
  /** The title of the file view. */
  readonly viewtitle: string = 'Fichiers';

  /** The number of items to display per page. */
  readonly itemsPerPage: number = 16;

  /** Array of status strings. */
  readonly status: string[] = Status.getStringList();

  /** Flag indicating whether to display files in grid layout or list layout. */
  gridlayout = true;

  /** Flag indicating whether the upload dialog is open or not. */
  isDialogOpen = false;

  /** Array of file headers to display. */
  files: FileHeader[] = [];

  /** The current page number. */
  currentPage = 1;

  /** The total number of elements. */
  numberOfElements!: number;

  /** Array currently defined keywords. */
  keywords!: string[];

  /** Array of types present in this folder  */
  types!: string[];

  /** The filename being searched.
   * Binded to the search field */
  filenameSearched = new FormControl('');

  /** Array of keywords being searched.
   * Binded to the search field */
  keywordsSearched = new FormControl<string[]>([]);

  /** Array of status strings being searched.
   * Binded to the search field */
  statusSearched = new FormControl<string[]>([]);

  /** The version being searched.
   * Binded to the search field */
  versionSearched = new FormControl('');

  /** The type being searched.
   * Binded to the search field */
  typeSearched = new FormControl<string[]>([]);

  /**
   * The date range being searched.
   */
  dateSearched = new FormControl<[Date, Date] | null>(null);

  /** Files currently selected */
  selectedFiles: Set<number> = new Set<number>();

  /** Button state for the multi-select button. */
  buttonMultiSelect: 'Empty' | 'Full' = 'Empty';

  /** Folder selected */
  folder!: string;
  displayableFolder!: string;

  /** Type of media selected */
  typeFolder!: string;
  displayableTypeFolder!: string;

  /** is extended search bar shown  */
  extendedSearch = false;

  constructor(
    private api: FileApiService,
    private fileService: FileListService,
    private route: ActivatedRoute,
    private router: Router,
    private confirmationDialog: ConfirmationDialogService,
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const currentUrl = this.router.url;
      const urlSegments = currentUrl.split('/');
      this.folder = urlSegments[urlSegments.length - 2];
      this.typeFolder = params['type'];
      this.displayableFolder = getNameFromPath(this.folder);
      this.displayableTypeFolder = getNameFromPath(this.typeFolder);
      this.currentPage = 1;
      this.refreshFileList();
    });

    this.fileService.files$.subscribe((files) => {
      this.files = files;
    });

    this.fileService.numberOfElements$.subscribe((n) => {
      this.numberOfElements = n;
    });

    this.fileService.keywords$.subscribe((keywords) => {
      this.keywords = keywords;
    });

    this.fileService.types$.subscribe((types) => {
      this.types = types;
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
    this.currentPage = pageNumber;
    this.unselect();
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
    this.fileService.fetchFiles(
      this.currentPage,
      this.itemsPerPage,
      this.folder,
      this.typeFolder,
      this.filenameSearched.value ?? '',
      this.keywordsSearched.value || undefined,
      (this.statusSearched.value || []).map((s) => Status.fromString(s)),
      this.versionSearched.value ?? undefined,
      this.typeSearched.value || undefined,
      this.dateSearched.value?.[0],
      this.dateSearched.value?.[1],
    );

    this.unselect();
  }

  fileSelected(index: number) {
    if (this.selectedFiles.has(index)) {
      this.selectedFiles.delete(index);
    } else {
      this.selectedFiles.add(index);
    }
    this.buttonMultiSelect =
      this.selectedFiles.size == this.files.length ? 'Full' : 'Empty';
  }

  onDeleteClicked() {
    this.confirmationDialog
      .openConfirmationDialog(
        'Confirmer la suppression',
        'Voulez-vous vraiment supprimer ' +
          (this.selectedFiles.size > 1 ? 'ces fichiers ?' : 'ce fichier ?'),
      )
      .then((confirmation) => {
        if (confirmation) {
          const deletePromises = [...this.selectedFiles].map((index) => {
            const filename = this.files[index].filename;
            return lastValueFrom(
              this.api.delete(this.folder + '/' + this.typeFolder, filename),
            );
          });

          Promise.all(deletePromises).then(() => {
            this.files = this.files.filter(
              (_, index) => !this.selectedFiles.has(index),
            );
            this.unselect();
            this.api
              .getNumberOfElement(
                this.folder + '/' + this.typeFolder,
                this.filenameSearched.value ?? '',
                this.keywordsSearched.value || undefined,
                (this.statusSearched.value || []).map((s) =>
                  Status.fromString(s),
                ),
                this.versionSearched.value ?? undefined,
                this.typeSearched.value || undefined,
                this.dateSearched.value?.[0],
                this.dateSearched.value?.[1],
              )
              .subscribe((n) => {
                this.numberOfElements = n;
              });
            this.api.getKeywords().subscribe((keywords) => {
              this.keywords = keywords;
            });
          });
        }
      });
  }

  onDuplicate(): void {
    const filenames = [...this.selectedFiles].map(
      (index) => this.files[index].filename,
    );
    filenames.forEach((filename) => {
      this.api
        .duplicate(this.folder + '/' + this.typeFolder, filename)
        .subscribe();
    });
  }

  onDownload(): void {
    for (const index of this.selectedFiles) {
      this.api
        .getFileData(
          this.folder + '/' + this.typeFolder,
          this.files[index].filename,
        )
        .subscribe({
          next: (blob) => {
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = this.files[index].filename;
            a.click();
            URL.revokeObjectURL(url);
          },
        });
    }
  }

  onShare(): void {
    const selectedFiles = [...this.selectedFiles].map(
      (index) => this.files[index],
    );
    const linkPromises = selectedFiles.map((file) =>
      lastValueFrom(
        this.api.getLink(this.folder + '/' + this.typeFolder, file.filename),
      ),
    );

    Promise.all(linkPromises).then((links) => {
      const mailtoLinks = links
        .map((link) => `- ${encodeURIComponent(link)}`)
        .join('%0A%0A');
      const mailtoLink = `mailto:?subject=Partage de fichier&body=Bonjour,%0A%0AVeuillez trouver ci-joint les liens vers les fichiers :%0A${mailtoLinks}`;
      window.location.href = mailtoLink;
    });
  }

  fileClicked(index: number): void {
    this.router.navigate(['app', 'file', this.files[index].id]);
  }

  onSelectClick(): void {
    this.buttonMultiSelect =
      this.buttonMultiSelect == 'Empty' ? 'Full' : 'Empty';
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

  unselect(): void {
    this.selectedFiles.clear();
    this.buttonMultiSelect = 'Empty';
  }

  search = (value: string) =>
    this.api.searchFolder(value, `${this.folder}/${this.typeFolder}`);

  goToFile(file: FileHeader) {
    this.fileService.fetchFiles(
      1,
      1,
      file.folder.split('/')[0],
      file.folder.split('/')[1],
      file.filename,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
    );
    this.router.navigate(['app', 'file', file.id]);
  }
}
