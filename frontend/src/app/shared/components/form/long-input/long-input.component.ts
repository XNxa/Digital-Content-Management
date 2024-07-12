import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-long-input',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './long-input.component.html',
  styleUrl: './long-input.component.css',
})
export class LongInputComponent {
  @Input() label = '';
  @Input() hint = '';
  @Input() placeholder = '';
  @Input() value!: FormControl<string | null>;
  @Input() border = true;
  @Input() rows = 3;
  @Input() disabled = false;
  @Output() valueChange: EventEmitter<FormControl<string | null>> =
    new EventEmitter<FormControl<string | null>>();

  isPasswordVisible = false;

  onInput(): void {
    this.valueChange.emit(this.value);
  }
}
