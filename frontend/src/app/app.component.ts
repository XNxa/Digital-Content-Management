import { AfterViewInit, Component, ViewChild, viewChild } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { SnackbarService } from './services/snackbar.service';
import { SnackbarComponent } from './shared/components/snackbar/snackbar.component';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HomeComponent, SnackbarComponent],
  providers: [],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements AfterViewInit {
  title = 'DCM';

  @ViewChild(SnackbarComponent) snackbarComponent!: SnackbarComponent;

  constructor(private snackbarService: SnackbarService) { }

  ngAfterViewInit(): void {
    this.snackbarService.setSnackbarComponent(this.snackbarComponent);
  }
}
