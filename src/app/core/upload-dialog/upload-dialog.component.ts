import { Component, EventEmitter, Output } from '@angular/core';
import { IconTextButtonComponent } from '../../shared/buttons/icon-text-button/icon-text-button.component';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { LongInputComponent } from '../../shared/form/long-input/long-input.component';
import { SelectComponent } from '../../shared/form/select/select.component';

@Component({
  selector: 'app-upload-dialog',
  standalone: true,
  imports: [
    IconTextButtonComponent,
    LongInputComponent,
    SelectComponent
  ],
  templateUrl: './upload-dialog.component.html',
  styleUrl: './upload-dialog.component.css'
})
export class UploadDialogComponent {
  title = "Importer un ficher";
  @Output() closeDialog: EventEmitter<void> = new EventEmitter<void>();
  @Output() fileSelected: EventEmitter<File> = new EventEmitter<File>();
  
  selectedFile : File | undefined;
  currentStep = 1;
  
  form : FormGroup = new FormGroup({
    description: new FormControl(),
  })

  close() {
    this.closeDialog.emit();
  }
  
  onBackdropClick(event: MouseEvent) {
    event.stopPropagation();
    this.close();
  }
  
  onDialogClick(event: MouseEvent) {
    event.stopPropagation();
  }
  
  onFileSelected($event: Event) {
    const input = ($event.target as HTMLInputElement);
    if ((input.files as FileList).length > 0) {
      const file = (input.files as FileList)[0];
      this.fileSelected.emit(file);
    }
  }
  
  cancel() {
    throw new Error('Method not implemented.');
  }
  
  nextStep() {
    this.currentStep = this.currentStep + 1;
  }

  previousStep() {
    this.currentStep = this.currentStep - 1;
  }

  save() {
    throw new Error('Method not implemented.');
  }
}