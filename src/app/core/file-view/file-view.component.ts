import { Component } from '@angular/core';
import { IconButtonComponent } from '../../shared/buttons/icon-button/icon-button.component';
import { FilecardComponent } from '../../shared/filecard/filecard.component';
import { PageSelectorComponent } from '../../page-selector/page-selector.component';

@Component({
  selector: 'app-file-view',
  standalone: true,
  imports: [IconButtonComponent, FilecardComponent, PageSelectorComponent],
  templateUrl: './file-view.component.html',
  styleUrl: './file-view.component.css'
})
export class FileViewComponent {
  viewtitle = "Fichier";
  
  onPageChange($event: number) {
    console.log($event);
  }
}
