import { Component, Input, OnInit } from '@angular/core';
import { FileHeader } from '../../../models/FileHeader';

@Component({
  selector: 'app-filecard',
  standalone: true,
  imports: [],
  templateUrl: './filecard.component.html',
  styleUrl: './filecard.component.css'
})
export class FilecardComponent implements OnInit {
  
  @Input() file : FileHeader | undefined;
  
  filename: string = '';
  type: string = '';

  isChecked = false;

  ngOnInit(): void {
    if (this.file && this.file?.filename.length > 12) {
      this.filename = this.file.filename.substring(0, 12) + '...';
    } else {
      this.filename = this.file?.filename!;
    }

    this.type = this.file?.type!;
    if (this.type.split("/").length == 2) {
      this.type = this.type.split("/")[1];
    }
  }

  onCardClicked(): void {
    this.isChecked = !this.isChecked;
  }
}
