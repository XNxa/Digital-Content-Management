import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
} from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ErrorMessageComponent } from '../error-message/error-message.component';

@Component({
  selector: 'app-input',
  standalone: true,
  imports: [ReactiveFormsModule, ErrorMessageComponent],
  templateUrl: './input.component.html',
  styleUrl: './input.component.css',
})
export class InputComponent implements OnChanges {
  @Input() label = '';
  @Input() placeholder = '';
  @Input() type: 'text' | 'password' = 'text';
  @Input() value!: FormControl<string | null>;
  @Input() border = true;
  @Input() disabled = false;
  @Input() autocomplete?: string[];
  @Output() valueChange: EventEmitter<FormControl<string | null>> =
    new EventEmitter<FormControl<string | null>>();
  @Output() changed: EventEmitter<null> = new EventEmitter<null>();

  isPasswordVisible = false;

  proposals: undefined | string[] = undefined;

  ngOnChanges(): void {
    if (this.autocomplete) {
      this.value.valueChanges.subscribe((v) => {
        this.proposals = this.autocomplete?.includes(v!)
          ? []
          : this.autocomplete?.filter((s) =>
              v ? s.toLowerCase().startsWith(v.toLowerCase()) : false,
            );
      });
    }
  }

  selectSuggestion(suggestion: string) {
    this.value.setValue(suggestion);
    this.changed.emit();
  }

  onInput(): void {
    this.changed.emit();
  }

  togglePasswordVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }
}
