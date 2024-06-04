import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { IconTextButtonComponent } from './shared/buttons/icon-text-button/icon-text-button.component';
import { FilecardComponent } from './shared/filecard/filecard.component';
import { IconButtonComponent } from './shared/buttons/icon-button/icon-button.component';
import { FileViewComponent } from './core/file-view/file-view.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, FileViewComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'DCM';
}
