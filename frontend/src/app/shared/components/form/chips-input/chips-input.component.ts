import { Component, EventEmitter, INJECTOR, Input, Output } from '@angular/core';
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
  @Input() suggestions: string[] = [];

  @Output() keywordsChange: EventEmitter<string[]> = new EventEmitter<string[]>();

  inputValue: string = '';
  filteredSuggestions: string[] = [];

  add(keyword: string): void {
    if (keyword && this.keywords.length < this.maxKeywords && !this.keywords.includes(keyword)) { 
      this.keywords.push(keyword.trim());
    }
    this.inputValue = '';
    this.filteredSuggestions = [];
    this.keywordsChange.emit(this.keywords);
  }

  onInputChange(): void {
    this.filteredSuggestions = this.suggestions.filter(s => 
      s.toLowerCase().includes(this.inputValue.toLowerCase()) && !this.keywords.includes(s)
    ).slice(0, 5);
  }

  selectSuggestion(suggestion: string): void {
    this.add(suggestion);
  }

  remove(keyword: string): void {
    const index = this.keywords.indexOf(keyword);

    if (index >= 0) {
      this.keywords.splice(index, 1);
    }
    this.keywordsChange.emit(this.keywords);
  }
}
