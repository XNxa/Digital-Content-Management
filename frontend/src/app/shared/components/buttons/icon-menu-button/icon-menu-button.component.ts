import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonIcon } from '../icon-button-type';

@Component({
  selector: 'app-icon-menu-button',
  standalone: true,
  imports: [],
  templateUrl: './icon-menu-button.component.html',
  styleUrl: './icon-menu-button.component.css'
})
export class IconMenuButtonComponent {
  @Input() icon!: ButtonIcon;
  @Input() disabled = false;
  @Input() color: 'blue' | 'white' | 'clearblue' = 'blue';

  @Input() buttonsLabels: string[] = [];
  @Input() buttonsIcons: ButtonIcon[] = [];

  @Output() click: EventEmitter<string> = new EventEmitter<string>();

  menuOpened: boolean = false;

  getIconPath(index: number): string {
    return 'icons/' + ((this.color == 'blue') ? 'white/' : 'blue/') + this.buttonsIcons[index] + '.svg';
  }

  handleClick(event: Event): void {
    event.stopPropagation();
    this.click.emit();
  }

  onMenuButtonClicked(index: number): void {
    this.click.emit(this.buttonsLabels[index]);
    this.menuOpened = false;
  }

  openMenu(): void {
    this.menuOpened = !this.menuOpened;
  }
}
