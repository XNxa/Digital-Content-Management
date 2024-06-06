import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-long-input',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './long-input.component.html',
  styleUrl: './long-input.component.css'
})
export class LongInputComponent {
  @Input() label: string = '';
  @Input() hint: string = '';
  @Input() placeholder: string = '';
  @Input() value: string = '';
  @Input() border = true;
  @Input() rows: number = 3;
  @Output() valueChange: EventEmitter<string> = new EventEmitter<string>();

  isPasswordVisible: boolean = false;

  onInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.valueChange.emit(input.value);
  }
}
