import { Component, EventEmitter, Output } from '@angular/core';
import { IconTextButtonComponent } from '../../shared/buttons/icon-text-button/icon-text-button.component';
import { LongInputComponent } from '../../shared/form/long-input/long-input.component';
import { SelectComponent } from '../../shared/form/select/select.component';
import { ChipsInputComponent } from '../../shared/form/chips-input/chips-input.component';
import { FileApiService } from '../../file-api.service';
import { Version } from '../../interfaces/version';
import { Status } from '../../interfaces/status';

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
  version: Version = 'VF'
  keywords: string[] = [];
  status: Status = 'Archivé'

  currentStep = 1;

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

    this.api.uploadFile(
      this.selectedFile as File,
      this.description, this.version,
      this.status,
      this.keywords
    ).subscribe({
      next: (response) => console.log(response),
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