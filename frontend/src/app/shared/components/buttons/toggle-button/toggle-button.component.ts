import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-toggle-button',
  standalone: true,
  imports: [],
  templateUrl: './toggle-button.component.html',
  styleUrl: './toggle-button.component.css'
})
export class ToggleButtonComponent {
  
  @Input() label: string = '';
  @Input() checked!: boolean;
  @Input() noTitle: boolean = false;
  @Output() checkedChange = new EventEmitter<boolean>();
  
  toggle() {
    this.checked = !this.checked;
    this.checkedChange.emit(this.checked);
  }
}
