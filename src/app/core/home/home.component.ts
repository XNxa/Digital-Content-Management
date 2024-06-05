import { Component } from '@angular/core';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { FileViewComponent } from '../file-view/file-view.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [SidebarComponent, FileViewComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

}
