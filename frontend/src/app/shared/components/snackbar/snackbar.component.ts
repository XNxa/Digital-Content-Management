import { Component } from '@angular/core';

@Component({
  selector: 'app-snackbar',
  standalone: true,
  imports: [],
  templateUrl: './snackbar.component.html',
  styleUrl: './snackbar.component.css'
})
export class SnackbarComponent {

  isVisible: boolean = false;
  message: string = '';

  show(message: string, duration: number = 3000) {
    this.message = message;
    this.isVisible = true;
    setTimeout(() => {
      this.isVisible = false;
    }, duration);
  }

}
