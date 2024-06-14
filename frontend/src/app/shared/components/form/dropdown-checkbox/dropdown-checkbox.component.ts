import { AfterContentInit, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-dropdown-checkbox',
  imports: [FormsModule],
  templateUrl: './dropdown-checkbox.component.html',
  styleUrls: ['./dropdown-checkbox.component.css']
})
export class DropdownCheckboxComponent {
  @Input() label: string = '';
  @Input() placeholder: string = '';
  @Input() options: string[] = [];

  @Input() value: string[] = [];
  @Output() valueChange: EventEmitter<string[]> = new EventEmitter<string[]>();

  selectedChips: string[] = [];
  dropdownOpen: boolean = false;
  filterText: string = '';
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
    this.valueChange.emit(this.selectedChips);
  }

  removeChip(index: number): void {
    this.selectedChips.splice(index, 1);
    this.filterOptions();
    this.valueChange.emit(this.selectedChips);
  }

  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
    if (this.dropdownOpen) {
      this.filteredOptions = this.options;
    }
  }

  filterOptions(): void {
    this.filteredOptions = this.options.filter(option =>
      option.toLowerCase().includes(this.filterText.toLowerCase())
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
