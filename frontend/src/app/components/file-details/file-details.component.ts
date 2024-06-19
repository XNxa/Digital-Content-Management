import { Component, Input, OnInit } from '@angular/core';
import { FileHeader } from '../../models/FileHeader';
import { MimeTypes } from '../../utils/mime-types';
import { FileApiService } from '../../services/file-api.service';

@Component({
  selector: 'app-file-details',
  standalone: true,
  imports: [],
  templateUrl: './file-details.component.html',
  styleUrl: './file-details.component.css'
})
export class FileDetailsComponent implements OnInit {

  @Input() file!: FileHeader;
  type!: string;
  displayable!: boolean;

  data!: string;

  constructor(private api : FileApiService) { }

  ngOnInit(): void {
    this.displayable = this.file.type.includes('image');

    if (this.displayable) {
      this.api.getFileData(this.file.filename).subscribe(data => {
        const reader = new FileReader();
        reader.readAsDataURL(data);
        reader.onload = () => {
          this.data = reader.result as string;
        }
      });
    };
    
    this.type = (MimeTypes.extension(this.file?.type!) || 'unknown').toUpperCase();
  }

}
