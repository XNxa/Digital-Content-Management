import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-chips-input',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './chips-input.component.html',
  styleUrl: './chips-input.component.css'
})
export class ChipsInputComponent {
  @Input() label: string = '';
  @Input() hint: string = '';
  @Input() keywords: string[] = [];
  @Input() maxKeywords: number = 20;

  inputValue: string = '';

  add(keyword: string): void {
    if (keyword && this.keywords.length < this.maxKeywords) {
      this.keywords.push(keyword.trim());
    }
    this.inputValue = '';
  }

  remove(keyword: string): void {
    const index = this.keywords.indexOf(keyword);

    if (index >= 0) {
      this.keywords.splice(index, 1);
    }
  }
}
