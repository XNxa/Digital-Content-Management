import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { Status } from '../../enums/status';
import { FileApiService } from '../../services/file-api.service';
import { SnackbarService } from '../../shared/components/snackbar/snackbar.service';
import { FileHeader } from '../../models/FileHeader';
import { IconTextButtonComponent } from '../../shared/components/buttons/icon-text-button/icon-text-button.component';
import { LongInputComponent } from '../../shared/components/form/long-input/long-input.component';
import { SelectComponent } from '../../shared/components/form/select/select.component';
import { ChipsInputComponent } from '../../shared/components/form/chips-input/chips-input.component';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-modify-dialog',
  standalone: true,
  imports: [
    IconTextButtonComponent,
    LongInputComponent,
    SelectComponent,
    ChipsInputComponent,
  ],
  templateUrl: './modify-dialog.component.html',
  styleUrl: './modify-dialog.component.css',
})
export class ModifyDialogComponent implements OnInit {
  title = 'Modifier les informations';

  @Input() file!: FileHeader;
  @Output() closeDialog: EventEmitter<void> = new EventEmitter<void>();

  description = new FormControl('');
  version = new FormControl('VF');
  keywords = new FormControl<string[]>([]);
  status = new FormControl('');
  fileType: string | undefined;
  thumbnail: string | undefined;

  keywordsSuggestions: string[] = [];
  readonly statusOptions = Status.getStringList();

  constructor(
    private api: FileApiService,
    private snackbar: SnackbarService,
  ) {}

  ngOnInit(): void {
    this.description.setValue(this.file?.description || '');
    this.version.setValue(this.file.version);
    this.keywords.setValue([...this.file.keywords]);
    this.status.setValue(
      Status.printableString(Status.fromString(this.file.status)),
    );
    this.fileType = this.file.type;

    if (this.fileType.includes('image')) {
      this.thumbnail = this.file.thumbnail;
    }

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

  save(): void {
    const metadata = this.file;
    metadata.description = this.description.value || '';
    metadata.version = this.version.value || '';
    metadata.keywords = this.keywords.value || [];
    metadata.status = Status.fromString(this.status.value || '');

    this.api.update(this.file.folder, this.file.filename, metadata).subscribe({
      next: () => {
        this.snackbar.show('Fichier modifié avec succès');
      },
    });

    this.close();
  }
}
