import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { CalendarComponent } from './calendar/calendar.component';

@Component({
  selector: 'app-date-input',
  standalone: true,
  imports: [CalendarComponent],
  templateUrl: './date-input.component.html',
  styleUrl: './date-input.component.css',
})
export class DateInputComponent {
  @Input() label = '';
  @Input() value!: FormControl<[Date, Date] | null>;
  @Output() valueChange: EventEmitter<FormControl<[Date, Date] | null>> =
    new EventEmitter<FormControl<[Date, Date] | null>>();

  calendarOpen = false;

  _startDate: Date | null = null;
  _endDate: Date | null = null;

  set startDate(d: Date | null) {
    this._startDate = d;
    if (this._startDate && this._endDate) {
      this.value.setValue([this._startDate, this._endDate]);
      this.valueChange.emit(this.value);
    } else {
      this.value.setValue(null);
      this.valueChange.emit(this.value);
    }
  }

  get startDate(): Date | null {
    return this._startDate;
  }

  set endDate(d: Date | null) {
    this._endDate = d;
    if (this._startDate && this._endDate) {
      this.value.setValue([this._startDate, this._endDate]);
      this.valueChange.emit(this.value);
    } else {
      this.value.setValue(null);
      this.valueChange.emit(this.value);
    }
  }

  get endDate(): Date | null {
    return this._endDate;
  }

  toggleCalendar(): void {
    this.calendarOpen = !this.calendarOpen;
  }

  closeCalendar(): void {
    this.calendarOpen = false;
  }

  printDates(startDate: Date | null, endDate: Date | null): string {
    if (!startDate || !endDate) {
      return '00/00/0000 - 00/00/0000';
    }

    return (
      startDate.getDate().toString() +
      '/' +
      startDate.getMonth().toString() +
      '/' +
      startDate.getFullYear().toString() +
      ' - ' +
      endDate.getDate().toString() +
      '/' +
      endDate.getMonth().toString() +
      '/' +
      endDate.getFullYear().toString()
    );
  }

  keyPress($event: KeyboardEvent) {
    switch ($event.key) {
      case 'Enter':
        this.calendarOpen = false;
        break;
      case 'Backspace':
        this.startDate = null;
        this.endDate = null;
        break;
      default:
        $event.stopPropagation();
        break;
    }
  }
}
