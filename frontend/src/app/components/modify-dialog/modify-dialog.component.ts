import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Status } from '../../enums/status';
import { FileApiService } from '../../services/file-api.service';
import { SnackbarService } from '../../services/snackbar.service';
import { FileHeader } from '../../models/FileHeader';
import { IconTextButtonComponent } from '../../shared/components/buttons/icon-text-button/icon-text-button.component';
import { LongInputComponent } from '../../shared/components/form/long-input/long-input.component';
import { SelectComponent } from '../../shared/components/form/select/select.component';
import { ChipsInputComponent } from '../../shared/components/form/chips-input/chips-input.component';

@Component({
  selector: 'app-modify-dialog',
  standalone: true,
  imports: [IconTextButtonComponent, LongInputComponent, SelectComponent, ChipsInputComponent],
  templateUrl: './modify-dialog.component.html',
  styleUrl: './modify-dialog.component.css'
})
export class ModifyDialogComponent {
  title = "Modifier les informations";

  @Input() file!: FileHeader;
  @Output() closeDialog: EventEmitter<void> = new EventEmitter<void>();

  description: string = '';
  version: string = 'VF'
  keywords: string[] = [];
  status: string = '';
  fileType: string | undefined;
  thumbnail: string | undefined;

  keywordsSuggestions: string[] = []
  readonly statusOptions = Status.getStringList();

  constructor(private api: FileApiService, private snackbar: SnackbarService) { }

  ngOnInit(): void {
    this.description = this.file?.description || '';
    this.version = this.file.version;
    this.keywords = [...this.file.keywords];
    this.status = this.file.status;
    this.fileType = this.file.type;

    if (this.fileType.includes('image')) {
      this.thumbnail = this.file.thumbnail;
    }

    this.api.getKeywords().subscribe({
      next: (keywords) => {
        this.keywordsSuggestions = keywords;
      },
      error: () => console.error()
    });
  }

  close(): void {
    this.closeDialog.emit();
  }

  onDialogClick(event: MouseEvent): void {
    event.stopPropagation();
  }

  save(): void {
    const metadata: FileHeader = {
      description: this.description,
      version: this.version,
      keywords: this.keywords,
      status: Status.fromString(this.status),
    } as FileHeader;

    this.api.update(this.file.filename, metadata).subscribe({
      next: () => {
        this.snackbar.show('Fichier modifié avec succès');
      },
      error: () => {
        this.snackbar.show('Erreur lors de la modification du fichier');
      }
    });

    this.close();
  }
}