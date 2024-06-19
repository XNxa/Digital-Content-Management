import { Component } from '@angular/core';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { FileListComponent } from '../file-list/file-list.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [SidebarComponent, FileListComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

}
