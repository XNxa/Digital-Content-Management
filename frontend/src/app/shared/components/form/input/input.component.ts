import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ErrorMessageComponent } from '../error-message/error-message.component';

@Component({
  selector: 'app-input',
  standalone: true,
  imports: [ReactiveFormsModule, ErrorMessageComponent],
  templateUrl: './input.component.html',
  styleUrl: './input.component.css'
})
export class InputComponent {
  @Input() label: string = '';
  @Input() placeholder: string = '';
  @Input() type: 'text' | 'password' = 'text';
  @Input() value!: FormControl<string | null>;
  @Input() border = true;
  @Output() valueChange: EventEmitter<FormControl<string | null>> = new EventEmitter<FormControl<string | null>>();
  @Output() onChange: EventEmitter<any> = new EventEmitter<any>();

  isPasswordVisible: boolean = false;

  onInput(event: Event): void {
    this.onChange.emit();
  }

  togglePasswordVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }
}
