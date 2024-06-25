import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FileHeader } from '../../../models/FileHeader';
import { StatusChipComponent } from '../status-chip/status-chip.component';
import { Status } from '../../../enums/status';
import { MimeTypes } from '../../../utils/mime-types';
import { getIconFor } from '../../../utils/file-icons';

@Component({
  selector: 'app-filecard',
  standalone: true,
  imports: [StatusChipComponent],
  templateUrl: './filecard.component.html',
  styleUrl: './filecard.component.css'
})
export class FilecardComponent implements OnInit {

  @Input() file: FileHeader | undefined;

  @Input() fileChecked !: boolean; 
  @Output() fileCheckedChange = new EventEmitter();
  @Output() fileClicked = new EventEmitter();

  filename: string = '';
  type: string = '';
  status: Status = Status.ARCHIVE;
  icon!: string;

  ngOnInit(): void {
    if (this.file && this.file?.filename.length > 12) {
      this.filename = this.file.filename.substring(0, 12) + '...';
    } else {
      this.filename = this.file?.filename!;
    }

    this.type = MimeTypes.extension(this.file?.type!) || 'unknown';
    this.icon = getIconFor(this.type);

    if (this.file?.status) {
      this.status = Status.fromString(this.file?.status);
    }
  }

  ngOnChanges(): void {
    this.ngOnInit();
  }

  onFileSelected(event : MouseEvent): void {
    event.stopPropagation();
    this.fileChecked = !this.fileChecked;
    this.fileCheckedChange.emit(this.file);
  }

  onCardClicked(): void {
    this.fileClicked.emit(this.file);
  }
}
