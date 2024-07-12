import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ErrorMessageComponent } from '../error-message/error-message.component';

@Component({
  selector: 'app-input',
  standalone: true,
  imports: [ReactiveFormsModule, ErrorMessageComponent],
  templateUrl: './input.component.html',
  styleUrl: './input.component.css',
})
export class InputComponent {
  @Input() label = '';
  @Input() placeholder = '';
  @Input() type: 'text' | 'password' = 'text';
  @Input() value!: FormControl<string | null>;
  @Input() border = true;
  @Input() disabled = false;
  @Output() valueChange: EventEmitter<FormControl<string | null>> =
    new EventEmitter<FormControl<string | null>>();
  @Output() changed: EventEmitter<null> = new EventEmitter<null>();

  isPasswordVisible = false;

  onInput(): void {
    this.changed.emit();
  }

  togglePasswordVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }
}
