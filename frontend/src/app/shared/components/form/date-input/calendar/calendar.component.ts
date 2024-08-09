import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
} from '@angular/core';

interface calendar {
  month: number;
  year: number;
  dates: Date[];
  side: 'left' | 'right';
}

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [],
  templateUrl: './calendar.component.html',
  styleUrl: './calendar.component.css',
})
export class CalendarComponent implements OnInit {
  monthNames = [
    'Janvier',
    'Février',
    'Mars',
    'Avril',
    'Mai',
    'Juin',
    'Juillet',
    'Août',
    'Septembre',
    'Octobre',
    'Novembre',
    'Décembre',
  ];
  weekDays = ['LUN', 'MAR', 'MER', 'JEU', 'VEN', 'SAM', 'DIM'];

  leftCalendar: calendar = {
    month: 0,
    year: 0,
    dates: [],
    side: 'left',
  };

  rightCalendar: calendar = {
    month: 0,
    year: 0,
    dates: [],
    side: 'right',
  };

  @Input() selectedStartDate: Date | null = null;
  @Input() selectedEndDate: Date | null = null;

  @Output() selectedStartDateChange: EventEmitter<Date | null> =
    new EventEmitter<Date | null>();
  @Output() selectedEndDateChange: EventEmitter<Date | null> =
    new EventEmitter<Date | null>();

  @Output() blurEvent: EventEmitter<void> = new EventEmitter<void>();

  @HostListener('document:click', ['$event'])
  onClick(event: MouseEvent): void {
    const targetElement = event.target as HTMLElement;
    if (!targetElement.closest('.date-selector')) {
      this.blurEvent.emit();
    }
  }

  @HostListener('document:keydown', ['$event'])
  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape' || event.key === 'Enter') {
      this.blurEvent.emit();
    }
  }

  constructor() {
    if (!(this.selectedStartDate && this.selectedEndDate)) {
      const today = new Date();
      this.leftCalendar.month = today.getMonth();
      this.leftCalendar.year = today.getFullYear();
      this.rightCalendar.month = today.getMonth();
      this.rightCalendar.year = today.getFullYear();
      this.nextMonth(this.rightCalendar);
    } else {
      this.leftCalendar.month = this.selectedStartDate.getMonth();
      this.leftCalendar.year = this.selectedStartDate.getFullYear();
      this.rightCalendar.month = this.selectedEndDate.getMonth();
      this.rightCalendar.year = this.selectedEndDate.getFullYear();
    }
  }

  ngOnInit(): void {
    this.generateCalendar(this.leftCalendar);
    this.generateCalendar(this.rightCalendar);
  }

  generateCalendar(calendar: calendar): void {
    calendar.dates = [];
    const firstDay = new Date(calendar.year, calendar.month, 0);
    const lastDay = new Date(calendar.year, calendar.month + 1, 0);

    for (let i = 1 - firstDay.getDay(); i <= lastDay.getDate(); i++) {
      calendar.dates.push(new Date(calendar.year, calendar.month, i));
    }
  }

  prevMonth(calendar: calendar): void {
    if (calendar.month == 0) {
      calendar.month = 11;
      calendar.year--;
    } else {
      calendar.month--;
    }
    if (calendar.side == 'right') {
      if (
        this.leftCalendar.month == calendar.month &&
        this.leftCalendar.year == calendar.year
      ) {
        this.prevMonth(this.leftCalendar);
      }
    }
    this.generateCalendar(calendar);
  }

  nextMonth(calendar: calendar): void {
    if (calendar.month == 11) {
      calendar.month = 0;
      calendar.year++;
    } else {
      calendar.month++;
    }
    if (calendar.side == 'left') {
      if (
        this.rightCalendar.month == calendar.month &&
        this.rightCalendar.year == calendar.year
      ) {
        this.nextMonth(this.rightCalendar);
      }
    }
    this.generateCalendar(calendar);
  }

  selectDate(date: Date): void {
    if (this.selectedStartDate && !this.selectedEndDate) {
      // Selecting the end date of the period
      if (date >= this.selectedStartDate) {
        this.selectedEndDate = date;
      } else {
        this.selectedEndDate = this.selectedStartDate;
        this.selectedStartDate = date;
      }
    } else {
      // Selecting the start date of the period
      this.selectedStartDate = date;
      this.selectedEndDate = null;
    }
    if (this.selectedStartDate && this.selectedEndDate) {
      this.selectedStartDateChange.emit(this.selectedStartDate);
      this.selectedEndDateChange.emit(this.selectedEndDate);
    }
  }

  isSelected(date: Date): string {
    if (
      this.equalDates(date, this.selectedStartDate) &&
      this.equalDates(date, this.selectedEndDate)
    ) {
      return 'selected';
    } else if (this.equalDates(date, this.selectedStartDate)) {
      return 'selected-left';
    } else if (this.equalDates(date, this.selectedEndDate)) {
      return 'selected-right';
    } else if (
      this.selectedStartDate &&
      this.selectedEndDate &&
      date > this.selectedStartDate &&
      date < this.selectedEndDate
    ) {
      return 'in-range';
    } else {
      return '';
    }
  }

  isHidden(date: Date, cal: calendar) {
    return date.getMonth() != cal.month || date.getFullYear() != cal.year
      ? 'hidden'
      : '';
  }

  private equalDates(date1: Date | null, date2: Date | null): boolean {
    return (
      (date1 &&
        date2 &&
        date1.getDate() == date2.getDate() &&
        date1.getMonth() == date2.getMonth() &&
        date1.getFullYear() == date2.getFullYear()) ??
      false
    );
  }
}
