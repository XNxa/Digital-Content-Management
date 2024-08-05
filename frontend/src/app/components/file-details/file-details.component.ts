import { Component, OnDestroy, OnInit } from '@angular/core';
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
import { IconTextButtonComponent } from '../../shared/components/buttons/icon-text-button/icon-text-button.component';
import { getNameFromPath } from '../../models/Tabs';
import { PermissionDirective } from '../../shared/directives/permission.directive';
import { ActivatedRoute, Router } from '@angular/router';
import { FileListService } from '../../services/file-list.service';

@Component({
  selector: 'app-file-details',
  standalone: true,
  imports: [
    ZoomButtonComponent,
    IconButtonComponent,
    IconTextButtonComponent,
    ModifyDialogComponent,
    StatusChipComponent,
    ZipListComponent,
    PermissionDirective,
  ],
  templateUrl: './file-details.component.html',
  styleUrl: './file-details.component.css',
})
export class FileDetailsComponent implements OnInit, OnDestroy {
  file!: FileHeader;

  folder!: string;
  typeFolder!: string;

  displayFolder!: string;
  displayTypeFolder!: string;

  type!: string;
  displaytype!: 'image' | 'video' | 'zip' | undefined;

  data!: string;
  currentZoom = 1;

  height: number | undefined;
  width: number | undefined;

  isDialogOpen = false;

  Status = Status;

  constructor(
    private api: FileApiService,
    private fileList: FileListService,
    private snackbar: SnackbarService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.api.getFile(params['id']).subscribe((file) => {
        this.file = file;
        [this.folder, this.typeFolder] = file.folder.split('/');

        if (this.file.type.startsWith('image')) {
          this.displaytype = 'image';
        } else if (this.file.type.startsWith('video')) {
          this.displaytype = 'video';
        } else if (this.file.type.includes('zip')) {
          this.displaytype = 'zip';
        } else {
          this.displaytype = undefined;
        }
        this.api.getFileData(this.file.folder, this.file.filename).subscribe({
          next: (blob) => {
            const fr = new FileReader();
            fr.readAsDataURL(blob);
            fr.onload = () => {
              this.data = fr.result as string;
            };
          },
        });

        this.type = (
          MimeTypes.extension(this.file?.type) || 'unknown'
        ).toUpperCase();
        this.displayFolder = getNameFromPath(this.folder);
        this.displayTypeFolder = getNameFromPath(this.typeFolder);
      });
    });
  }

  ngOnDestroy(): void {
    URL.revokeObjectURL(this.data);
  }

  openDialog(): void {
    this.isDialogOpen = true;
  }

  zoomChange(zoom: number): void {
    this.currentZoom = zoom;
  }

  onSendByEmail(): void {
    this.api.getLink(this.file.folder, this.file.filename).subscribe((link) => {
      const mailtoLink = `mailto:?subject=Partage de fichier&body=Bonjour,%0A%0AVeuillez trouver ci-joint le lien vers le fichier :%0A${encodeURIComponent(link)}`;
      window.location.href = mailtoLink;
    });
  }

  onGetLink(): void {
    this.api.getLink(this.file.folder, this.file.filename).subscribe((link) => {
      navigator.clipboard.writeText(link).then(() => {
        this.snackbar.show('Lien copié dans le presse-papier');
      });
    });
  }

  onDownload(): void {
    const a = document.createElement('a');
    a.href = this.data;
    a.download = this.file.filename;
    a.click();
  }

  onDuplicate(): void {
    this.api.duplicate(this.file.folder, this.file.filename).subscribe();
  }

  onDelete(): void {
    this.api.delete(this.file.folder, this.file.filename).subscribe(() => {
      this.fileList.remove(this.file.id);
      this.onPrevious();
    });
  }

  onClose(): void {
    this.router.navigateByUrl('app/' + this.file.folder);
  }

  onPrevious() {
    this.router.navigateByUrl(
      'app/file/' + this.fileList.previous(this.file.id),
    );
  }

  onNext() {
    this.router.navigateByUrl('app/file/' + this.fileList.next(this.file.id));
  }
}
