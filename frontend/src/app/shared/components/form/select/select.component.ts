import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ErrorMessageComponent } from '../error-message/error-message.component';

@Component({
  selector: 'app-select',
  standalone: true,
  imports: [ReactiveFormsModule, ErrorMessageComponent],
  templateUrl: './select.component.html',
  styleUrl: './select.component.css'
})
export class SelectComponent implements OnInit {
  @Input() label: string = '';
  @Input() hint: string = '';
  @Input() options!: string[];
  @Input() border = true;
  @Input() disabled = false;
  @Input() value!: FormControl<string | null>;
  @Output() valueChange: EventEmitter<FormControl<string | null>> = new EventEmitter<FormControl<string | null>>();

  ngOnInit(): void {
    if (this.options) {
      const i = this.options.findIndex((option) => option == this.value.value);
      if (i == -1) {
        if (this.value.value)
          this.options.unshift(this.value.value);
      } else if (i != 0) {
        if (this.value.value) {
          this.options.splice(i, 1);
          this.options.unshift(this.value.value);
        }
      }
      this.value.setValue(this.options[0]);
    }
  }

  onInput(_event: Event): void {
    this.valueChange.emit(this.value);
  }

}
