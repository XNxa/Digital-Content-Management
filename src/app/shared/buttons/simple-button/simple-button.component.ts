import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-simple-button',
  standalone: true,
  imports: [],
  templateUrl: './simple-button.component.html',
  styleUrl: './simple-button.component.css'
})
export class SimpleButtonComponent {
  @Input() title = '';
  @Input() disabled = false;
  @Output() click = new EventEmitter();
}
