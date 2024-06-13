import { Component, Input, OnInit } from '@angular/core';
import { FileHeader } from '../../../models/FileHeader';
import { StatusChipComponent } from '../status-chip/status-chip.component';
import { Status } from '../../../enums/status';
import { MimeTypes } from '../../../utils/mime-types';

@Component({
  selector: 'app-filecard',
  standalone: true,
  imports: [StatusChipComponent],
  templateUrl: './filecard.component.html',
  styleUrl: './filecard.component.css'
})
export class FilecardComponent implements OnInit {

  @Input() file: FileHeader | undefined;

  filename: string = '';
  type: string = '';
  status: Status = Status.ARCHIVE;

  isChecked = false;

  ngOnInit(): void {
    if (this.file && this.file?.filename.length > 12) {
      this.filename = this.file.filename.substring(0, 12) + '...';
    } else {
      this.filename = this.file?.filename!;
    }

    this.type = MimeTypes.extension(this.file?.type!) || 'unknown';

    if (this.file?.status) {
      this.status = Status.fromString(this.file?.status);
    }
  }

  onCardClicked(): void {
    this.isChecked = !this.isChecked;
  }
}
