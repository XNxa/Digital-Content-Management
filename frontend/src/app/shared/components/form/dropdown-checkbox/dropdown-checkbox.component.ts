import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-dropdown-checkbox',
  imports: [FormsModule],
  templateUrl: './dropdown-checkbox.component.html',
  styleUrls: ['./dropdown-checkbox.component.css'],
})
export class DropdownCheckboxComponent {
  @Input() label = '';
  @Input() placeholder = '';
  @Input() options: string[] = [];

  @Input() value!: FormControl<string[] | null>;
  @Output() valueChange: EventEmitter<FormControl<string[] | null>> =
    new EventEmitter<FormControl<string[] | null>>();

  selectedChips: string[] = [];
  dropdownOpen = false;
  filterText = '';
  filteredOptions!: string[];

  isSelected(option: string): boolean {
    return this.selectedChips.includes(option);
  }

  toggleSelection(option: string): void {
    const index = this.selectedChips.indexOf(option);
    if (index > -1) {
      this.selectedChips.splice(index, 1);
    } else {
      this.selectedChips.push(option);
    }
    this.value.setValue(this.selectedChips);
    this.valueChange.emit(this.value);
  }

  removeChip(index: number): void {
    this.selectedChips.splice(index, 1);
    this.filterOptions();
    this.value.setValue(this.selectedChips);
    this.valueChange.emit(this.value);
  }

  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
    if (this.dropdownOpen) {
      this.filteredOptions = this.options;
    }
  }

  filterOptions(): void {
    this.filteredOptions = this.options.filter((option) =>
      option.toLowerCase().includes(this.filterText.toLowerCase()),
    );
  }

  stopprop($event: MouseEvent): void {
    $event.stopPropagation();
  }

  exit($event: KeyboardEvent): void {
    if ($event.key === 'Escape') {
      this.dropdownOpen = false;
      this.filterText = '';
      this.filterOptions();
    }
  }
}
