import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonIcon } from '../icon-button-type';

@Component({
  selector: 'app-icon-button',
  standalone: true,
  imports: [],
  templateUrl: './icon-button.component.html',
  styleUrl: './icon-button.component.css',
})
export class IconButtonComponent {
  @Input() icon!: ButtonIcon;
  @Input() disabled = false;
  @Input() color: 'blue' | 'white' | 'clearblue' | 'dark' = 'blue';
  @Output() click: EventEmitter<void> = new EventEmitter<void>();

  getIconPath(): string {
    return (
      'icons/' +
      (this.color == 'blue' || this.color == 'dark' ? 'white/' : 'blue/') +
      this.icon +
      '.svg'
    );
  }

  handleClick(event: Event): void {
    event.stopPropagation();
    this.click.emit();
  }
}
