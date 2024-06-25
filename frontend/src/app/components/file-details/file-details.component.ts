import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FileHeader } from '../../models/FileHeader';
import { MimeTypes } from '../../utils/mime-types';
import { FileApiService } from '../../services/file-api.service';
import { ZoomButtonComponent } from '../../shared/components/buttons/zoom-button/zoom-button.component';
import { IconButtonComponent } from '../../shared/components/buttons/icon-button/icon-button.component';
import { ModifyDialogComponent } from '../modify-dialog/modify-dialog.component';
import { SnackbarService } from '../../shared/components/snackbar/snackbar.service';
import { Status } from '../../enums/status';
import { StatusChipComponent } from '../../shared/components/status-chip/status-chip.component';

@Component({
  selector: 'app-file-details',
  standalone: true,
  imports: [ZoomButtonComponent, IconButtonComponent, ModifyDialogComponent, StatusChipComponent],
  templateUrl: './file-details.component.html',
  styleUrl: './file-details.component.css',
})
export class FileDetailsComponent implements OnInit, OnChanges {
  @Input() file!: FileHeader;

  @Output() closeView: EventEmitter<void> = new EventEmitter<void>();

  @Output() previous: EventEmitter<void> = new EventEmitter<void>();
  @Output() next: EventEmitter<void> = new EventEmitter<void>();

  type!: string;
  displayable!: boolean;

  data!: string;
  currentZoom: number = 1;

  height: number | undefined;
  width: number | undefined;

  isDialogOpen: boolean = false;

  Status = Status;

  constructor(private api: FileApiService, private snackbar: SnackbarService) { }
  
  ngOnInit(): void {
    this.displayable = this.file.type.includes('image');

    if (this.displayable) {
      this.api.getFileData(this.file.filename).subscribe(data => {
        const img = new Image();
        img.src = URL.createObjectURL(data);
        img.onload = () => {
          this.width = img.width;
          this.height = img.height;
        };
        
        const reader = new FileReader();
        reader.readAsDataURL(data);
        reader.onload = () => {
          this.data = reader.result as string;
        }
      });
    };
    
    this.type = (MimeTypes.extension(this.file?.type!) || 'unknown').toUpperCase();
  }

  ngOnChanges(_changes: SimpleChanges): void {
    this.ngOnInit();
  }

  openDialog(): void {
    this.isDialogOpen = true;
  }

  zoomChange(zoom: number): void {
    this.currentZoom = zoom;
  }

  onSendByEmail(): void {
    this.api.getLink(this.file.filename).subscribe(link => {
      const mailtoLink = `mailto:?subject=Partage de fichier&body=Bonjour,%0A%0AVeuillez trouver ci-joint le lien vers le fichier :%0A${encodeURIComponent(link)}`;
      window.location.href = mailtoLink;
    });
  }

  onGetLink(): void {
    this.api.getLink(this.file.filename).subscribe(link => {
      navigator.clipboard.writeText(link).then(() => {
        this.snackbar.show('Lien copié dans le presse-papier');
      });
    });
  }

  onDownload(): void {
    this.api.getFileData(this.file.filename).subscribe({
      next: blob => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = this.file.filename;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: () => {
        this.snackbar.show('Erreur lors du téléchargement');
      }
    });
  }

  onDuplicate(): void {
    this.api.duplicate(this.file.filename).subscribe({
      next: () => {
        this.snackbar.show('Fichier dupliqué avec succès');
      },
      error: () => {
        this.snackbar.show('Erreur lors de la duplication du fichier');
      }
    });
  }

  onDelete(): void {
    this.api.delete([this.file.filename]).subscribe({
      next: () => {
        this.snackbar.show('Fichiers supprimés avec succès');
      },
      error: () => {
        this.snackbar.show('Erreur lors de la suppression des fichiers');
      }
    });
    this.next.emit();
  }

  onClose(): void {
    this.closeView.emit();
  }

  onPrevious() {
    this.previous.emit();
  }

  onNext() {
    this.next.emit();
  }
}
