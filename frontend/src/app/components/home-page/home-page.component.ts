import { Component } from '@angular/core';
import { IconButtonComponent } from '../../shared/components/buttons/icon-button/icon-button.component';
import { SmallCardComponent } from '../../shared/components/small-card/small-card.component';
import { FilesStateComponent } from './files-state/files-state.component';
import { FileApiService } from '../../services/file-api.service';
import { SearchBarComponent } from '../../shared/components/search-bar/search-bar.component';
import { User, UserFilter } from '../../models/User';
import { UserApiService } from '../../services/user-api.service';
import { environment } from '../../../environments/environment.development';
import { FileHeader } from '../../models/FileHeader';
import { FileListService } from '../../services/file-list.service';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [
    IconButtonComponent,
    SmallCardComponent,
    FilesStateComponent,
    SearchBarComponent
  ],
  templateUrl: './home-page.component.html',
  styleUrl: './home-page.component.css',
})
export class HomePageComponent {
  currentUser!: User;
  constructor(
    private api: FileApiService,
    private auth: AuthService,
    private userapi: UserApiService,
    private fileList: FileListService,
    private router: Router,
  ) {}

  enable_search = environment.elasticsearch_enabled;

  search = (value: string) => this.api.search(value);

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
    const filter: UserFilter = {
      email: this.auth.getUsername(),
      firstname: undefined,
      lastname: undefined,
      function: undefined,
      role: undefined,
      statut: undefined,
      password: undefined,
    };
    this.userapi.getUsers(0, 1, filter).subscribe((user) => {
      if (user.length === 1) {
        this.currentUser = user[0];
      }
    });
  }

  goToFile(file: FileHeader) {
    this.fileList.fetchFiles(
      1,
      1,
      file.folder.split('/')[0],
      file.folder.split('/')[1],
      file.filename,
      undefined,
      undefined,
      undefined, 
      undefined, 
      undefined,
      undefined,
    )
    this.router.navigate(['app', 'file', file.id ])
  }
}
