import { ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FileHeader } from '../../models/FileHeader';
import { MimeTypes } from '../../utils/mime-types';
import { FileApiService } from '../../services/file-api.service';
import { ZoomButtonComponent } from '../../shared/components/buttons/zoom-button/zoom-button.component';
import { IconButtonComponent } from '../../shared/components/buttons/icon-button/icon-button.component';
import { ModifyDialogComponent } from '../modify-dialog/modify-dialog.component';
import { SnackbarService } from '../../shared/components/snackbar/snackbar.service';
import { Status } from '../../enums/status';
import { StatusChipComponent } from '../../shared/components/status-chip/status-chip.component';
import { ZipListComponent } from '../../shared/components/zip-list/zip-list.component';

@Component({
  selector: 'app-file-details',
  standalone: true,
  imports: [ZoomButtonComponent, IconButtonComponent, ModifyDialogComponent, StatusChipComponent, ZipListComponent],
  templateUrl: './file-details.component.html',
  styleUrl: './file-details.component.css',
})
export class FileDetailsComponent implements OnChanges {
  @Input() file!: FileHeader;

  @Output() closeView: EventEmitter<void> = new EventEmitter<void>();

  @Output() previous: EventEmitter<void> = new EventEmitter<void>();
  @Output() next: EventEmitter<void> = new EventEmitter<void>();

  type!: string;
  displaytype!: 'image' | 'video' | 'zip' | undefined;

  data!: string;
  currentZoom: number = 1;

  height: number | undefined;
  width: number | undefined;

  isDialogOpen: boolean = false;

  Status = Status;

  constructor(private api: FileApiService, private snackbar: SnackbarService) { }

  ngOnChanges(_changes: SimpleChanges): void {
    if (this.file.type.startsWith('image')) {
      this.displaytype = 'image';
    } else if (this.file.type.startsWith('video')) {
      this.displaytype = 'video';
    } else if (this.file.type.includes('zip')) {
      this.displaytype = 'zip';
    } else {
      this.displaytype = undefined;
    }

    this.api.getFileData(this.file.filename).subscribe({
      next: blob => {
        let fr = new FileReader();
        fr.readAsDataURL(blob);
        fr.onload = () => {
          this.data = fr.result as string;
        }
      },
      error: () => {
        this.snackbar.show('Erreur lors du chargement du fichier');
      }
    });

    this.type = (MimeTypes.extension(this.file?.type!) || 'unknown').toUpperCase();
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
