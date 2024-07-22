import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css',
})
export class SearchBarComponent {
  open = false;
  hover = false;

  value: string = '';

  updateSearch() {
    console.log('searching for', this.value);
  }

  setHover(value: boolean) {
    if (this.open) {
      return;
    }
    this.hover = value;
  }

  enterFocus() {
    this.open = true;
  }

  exitFocus() {
    this.open = false;
    this.hover = false;
    this.value = '';
  }

  toggleFocus() {
    if (this.open) {
      this.exitFocus();
    } else {
      this.enterFocus();
      this.hover = true;
    }
  }
}
