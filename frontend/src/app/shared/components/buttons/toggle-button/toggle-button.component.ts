import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-toggle-button',
  standalone: true,
  imports: [],
  templateUrl: './toggle-button.component.html',
  styleUrl: './toggle-button.component.css',
})
export class ToggleButtonComponent {
  @Input() label = '';
  @Input() checked!: boolean;
  @Input() noTitle = false;
  @Input() disabled = false;
  @Output() checkedChange = new EventEmitter<boolean>();

  toggle($event: MouseEvent) {
    if (this.disabled) return;
    $event.stopPropagation();
    this.checked = !this.checked;
    this.checkedChange.emit(this.checked);
  }
}
