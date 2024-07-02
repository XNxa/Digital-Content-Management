import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-long-input',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './long-input.component.html',
  styleUrl: './long-input.component.css'
})
export class LongInputComponent {
  @Input() label: string = '';
  @Input() hint: string = '';
  @Input() placeholder: string = '';
  @Input() value!: FormControl<string | null>;
  @Input() border = true;
  @Input() rows: number = 3;
  @Output() valueChange: EventEmitter<FormControl<string | null>> = new EventEmitter<FormControl<string | null>>();

  isPasswordVisible: boolean = false;

  onInput(_event: Event): void {
    this.valueChange.emit(this.value);
  }
}
