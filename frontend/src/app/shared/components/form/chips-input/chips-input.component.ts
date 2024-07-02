import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-chips-input',
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule],
  templateUrl: './chips-input.component.html',
  styleUrl: './chips-input.component.css'
})
export class ChipsInputComponent {
  @Input() label: string = '';
  @Input() hint: string = '';
  @Input() keywords!: FormControl<string[] |  null>;
  @Input() suggestions: string[] = [];

  @Output() keywordsChange: EventEmitter<FormControl<string[] |  null>> = new EventEmitter<FormControl<string[] |  null>>();

  inputValue: string = '';
  filteredSuggestions: string[] = [];

  console = console;

  add(keyword: string): void {
    if (keyword && !(this.keywords.value || []).includes(keyword)) { 
      (this.keywords.value || []).push(keyword.trim());
    }

    this.inputValue = '';
    this.filteredSuggestions = [];
    this.keywordsChange.emit(this.keywords);
  }

  onInputChange(): void {
    this.filteredSuggestions = this.suggestions.filter(s => 
      s.toLowerCase().includes(this.inputValue.toLowerCase()) && !(this.keywords.value || []).includes(s)
    ).slice(0, 5);
  }

  selectSuggestion(suggestion: string): void {
    this.add(suggestion);
  }

  remove(keyword: string): void {
    const index = (this.keywords.value || []).indexOf(keyword);

    if (index >= 0) {
      (this.keywords.value || []).splice(index, 1);
    }
    this.keywordsChange.emit(this.keywords);
  }
}
