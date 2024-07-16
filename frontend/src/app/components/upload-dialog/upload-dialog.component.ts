import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IconTextButtonComponent } from '../../shared/components/buttons/icon-text-button/icon-text-button.component';
import { LongInputComponent } from '../../shared/components/form/long-input/long-input.component';
import { SelectComponent } from '../../shared/components/form/select/select.component';
import { ChipsInputComponent } from '../../shared/components/form/chips-input/chips-input.component';
import { FileApiService } from '../../services/file-api.service';
import { Status } from '../../enums/status';
import { FileHeader } from '../../models/FileHeader';
import { SnackbarService } from '../../shared/components/snackbar/snackbar.service';
import { MimeTypes } from '../../utils/mime-types';
import { FormControl } from '@angular/forms';
import { getTabForType } from '../../models/Tabs';

@Component({
  selector: 'app-upload-dialog',
  standalone: true,
  imports: [
    IconTextButtonComponent,
    LongInputComponent,
    SelectComponent,
    ChipsInputComponent,
  ],
  templateUrl: './upload-dialog.component.html',
  styleUrl: './upload-dialog.component.css',
})
export class UploadDialogComponent implements OnInit {
  title = 'Importer un ficher';
  @Output() closeDialog: EventEmitter<void> = new EventEmitter<void>();

  @Input() folder!: string;
  @Input() typeFolder!: string;
  selectedFile: File | undefined;
  description = new FormControl('');
  version = new FormControl('VF');
  keywords = new FormControl<string[]>([]);
  currentStep = 1;

  keywordsSuggestions: string[] = [];

  readonly statusOptions = Status.getStringList();
  status = new FormControl(Status.printableString(Status.PLANIFIE));

  fileType: string | undefined;

  constructor(
    private api: FileApiService,
    private snackbar: SnackbarService,
  ) {}

  ngOnInit(): void {
    this.api.getKeywords().subscribe({
      next: (keywords) => {
        this.keywordsSuggestions = keywords;
      },
    });
  }

  close(): void {
    this.closeDialog.emit();
  }

  onDialogClick(event: MouseEvent): void {
    event.stopPropagation();
  }

  onFileSelected($event: Event): void {
    const input = $event.target as HTMLInputElement;
    if (input.files != null) {
      if (input.files.length > 0) {
        const file = input.files[0];
        this.selectedFile = file;

        new Promise<string>((resolve) => {
          const mimeType = MimeTypes.lookup(file.name);
          if (mimeType) {
            resolve(mimeType);
          } else {
            resolve('application/octet-stream');
          }
        }).then((mimeType) => {
          this.fileType = mimeType;
        });

        if (file.size > 50_000_000) {
          this.snackbar.show('Fichier trop volumineux');
          this.selectedFile = undefined;
        }
      }
    }
  }

  nextStep(): void {
    if (this.currentStep == 1) {
      if (!this.selectedFile) {
        this.snackbar.show('Aucun fichier sélectionné !');
        return;
      } else if (getTabForType(this.selectedFile.type) != this.typeFolder) {
        this.snackbar.show('Le type de fichier ne correspond pas au dossier !');
        this.selectedFile = undefined;
        return;
      }
    }
    this.currentStep = this.currentStep + 1;
  }

  previousStep(): void {
    this.selectedFile = undefined;
    this.currentStep = this.currentStep - 1;
  }

  save(): void {
    if (!this.selectedFile) {
      console.error('No file selected!');
      return;
    }

    const metadata: FileHeader = {
      folder: this.folder + '/' + this.typeFolder, // TODO
      filename: this.selectedFile.name,
      description: this.description.value,
      version: this.version.value,
      keywords: this.keywords.value,
      status: Status.fromString(this.status.value || ''),
      type: this.fileType || 'application/octet-stream',
      size: this.selectedFile.size,
    } as FileHeader;

    this.api.uploadFile(this.selectedFile, metadata).subscribe({
      next: () => {
        this.api.getKeywords().subscribe({
          next: (keywords) => {
            this.keywordsSuggestions = keywords;
          },
        });
      },
    });

    this.selectedFile = undefined;
    this.close();
  }

  cancel(): void {
    this.selectedFile = undefined;
    this.close();
  }
}
