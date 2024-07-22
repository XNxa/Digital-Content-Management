import { Component } from '@angular/core';
import { IconButtonComponent } from '../../shared/components/buttons/icon-button/icon-button.component';
import { SmallCardComponent } from '../../shared/components/small-card/small-card.component';
import { FilesStateComponent } from './files-state/files-state.component';
import { FileApiService } from '../../services/file-api.service';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [IconButtonComponent, SmallCardComponent, FilesStateComponent],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css',
})
export class HomePageComponent {
  constructor(private api: FileApiService) {}

  images!: number;
  videos!: number;
  pictos!: number;
  docs!: number;

  ngOnInit() {
    this.api.getNewFiles().subscribe((values) => {
      this.images = values[0];
      this.videos = values[1];
      this.pictos = values[2];
      this.docs = values[3];
    });
  }
}
