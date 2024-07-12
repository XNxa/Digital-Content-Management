import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonIcon } from '../icon-button-type';

@Component({
  selector: 'app-icon-text-button',
  standalone: true,
  imports: [],
  templateUrl: './icon-text-button.component.html',
  styleUrl: './icon-text-button.component.css',
})
export class IconTextButtonComponent {
  @Input() title = '';
  @Input() disabled = false;
  @Input() icon!: ButtonIcon;
  @Input() color: 'blue' | 'white' | 'clearblue' = 'blue';
  @Output() click = new EventEmitter();

  getIconPath(): string {
    return (
      'icons/' +
      (this.color == 'blue' ? 'white/' : 'blue/') +
      this.icon +
      '.svg'
    );
  }

  handleClick(event: Event): void {
    event.stopPropagation();
    this.click.emit();
  }
}
