import { O } from '@angular/cdk/keycodes';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-toggle-button',
  standalone: true,
  imports: [],
  templateUrl: './toggle-button.component.html',
  styleUrl: './toggle-button.component.css'
})
export class ToggleButtonComponent {
  
  @Input() label: string = '';
  @Input() checked!: FormControl<boolean | null> ;
  @Output() checkedChange = new EventEmitter<FormControl<boolean | null>>();
  
  toggle() {
    this.checked.setValue(!this.checked.value);
    this.checkedChange.emit(this.checked);
  }
}
