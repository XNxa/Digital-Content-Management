import { Component, EventEmitter, Output } from '@angular/core';
import { IconTextButtonComponent } from '../../shared/components/buttons/icon-text-button/icon-text-button.component';
import { LongInputComponent } from '../../shared/components/form/long-input/long-input.component';
import { SelectComponent } from '../../shared/components/form/select/select.component';
import { ChipsInputComponent } from '../../shared/components/form/chips-input/chips-input.component';
import { FileApiService } from '../../file-api.service';
import { Status } from '../../enums/status';
import { FileHeader } from '../../models/FileHeader';

@Component({
  selector: 'app-upload-dialog',
  standalone: true,
  imports: [
    IconTextButtonComponent,
    LongInputComponent,
    SelectComponent,
    ChipsInputComponent
  ],
  templateUrl: './upload-dialog.component.html',
  styleUrl: './upload-dialog.component.css'
})
export class UploadDialogComponent {
  title = "Importer un ficher";
  @Output() closeDialog: EventEmitter<void> = new EventEmitter<void>();

  selectedFile: File | undefined;
  description: string = '';
  version: string = 'VF'
  keywords: string[] = [];
  currentStep = 1;
  
  readonly statusOptions = Status.getStringList();
  status : string = Status.printableString(Status.ARCHIVE);

  constructor(private api: FileApiService) { }

  close(): void {
    this.closeDialog.emit();
  }

  onDialogClick(event: MouseEvent): void {
    event.stopPropagation();
  }

  onFileSelected($event: Event): void {
    const input = ($event.target as HTMLInputElement);
    if (input.files != null) {
      if (input.files.length > 0) {
        const file = input.files[0];
        this.selectedFile = file;
      }
    }
  }

  nextStep(): void {
    this.currentStep = this.currentStep + 1;
  }

  previousStep(): void {
    this.selectedFile = undefined;
    this.currentStep = this.currentStep - 1;
  }

  save(): void {
    if (!this.selectedFile) {
      console.error("No file selected!");
      return;
    }

    const metadata : FileHeader = {
      description: this.description,
      version: this.version,
      keywords: this.keywords,
      status: Status.fromString(this.status)
    } as FileHeader;

    this.api.uploadFile(this.selectedFile, metadata).subscribe({
      next: () => console.log(),
      error: () => console.error()
    });

    this.selectedFile = undefined;
    this.close();
  }

  cancel(): void {
    this.selectedFile = undefined;
    this.close();
  }
} 