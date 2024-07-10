import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ErrorMessageComponent } from '../error-message/error-message.component';

@Component({
  selector: 'app-select',
  standalone: true,
  imports: [ReactiveFormsModule, ErrorMessageComponent],
  templateUrl: './select.component.html',
  styleUrl: './select.component.css'
})
export class SelectComponent implements OnChanges {
  @Input() label: string = '';
  @Input() hint: string = '';
  @Input() options!: string[];
  @Input() border = true;
  @Input() disabled = false;
  @Input() value!: FormControl<string | null>;
  @Output() valueChange: EventEmitter<FormControl<string | null>> = new EventEmitter<FormControl<string | null>>();

  ngOnChanges(changes: SimpleChanges): void {
    if (this.options && !this.options.includes('')) {
      this.options.unshift('');
    }

    if (changes['disabled']) {
      if (!this.disabled) {
        this.value.setValue(null)
        this.valueChange.emit(this.value);
      }
    }
  }

  onInput(_event: Event): void {
    this.valueChange.emit(this.value);
  }
}
