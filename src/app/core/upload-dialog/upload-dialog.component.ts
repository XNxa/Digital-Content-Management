import { Component, EventEmitter, Output } from '@angular/core';
import { IconTextButtonComponent } from '../../shared/buttons/icon-text-button/icon-text-button.component';
import { LongInputComponent } from '../../shared/form/long-input/long-input.component';
import { SelectComponent } from '../../shared/form/select/select.component';
import { ChipsInputComponent } from '../../shared/form/chips-input/chips-input.component';
import { FileApiService } from '../../file-api.service';

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
  
  selectedFile : File | undefined;
  currentStep = 1;

  constructor(private api : FileApiService) { }

  close() {
    this.closeDialog.emit();
  }
  
  onDialogClick(event: MouseEvent) {
    event.stopPropagation();
  }
  
  onFileSelected($event: Event) {
    const input = ($event.target as HTMLInputElement);
    if (input.files != null) {
      if (input.files.length > 0) {
        const file = input.files[0];
        this.selectedFile = file;
      }
    }
  }
  
  nextStep() {
    this.currentStep = this.currentStep + 1;
  }
  
  previousStep() {
    this.selectedFile = undefined;
    this.currentStep = this.currentStep - 1;
  }
  
  save() {
    this.api.uploadFile(this.selectedFile as File).subscribe({
      next: (response) => console.log(response),
      error: (error) => console.error(error)
    });
  }
  
  cancel() {
    this.selectedFile = undefined;
    this.close();
  }
} 