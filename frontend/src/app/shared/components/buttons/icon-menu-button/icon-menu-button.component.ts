import { Component, Input } from '@angular/core';
import { ButtonIcon } from '../icon-button-type';

@Component({
  selector: 'app-icon-menu-button',
  standalone: true,
  imports: [],
  templateUrl: './icon-menu-button.component.html',
  styleUrl: './icon-menu-button.component.css',
})
export class IconMenuButtonComponent {
  @Input() icon!: ButtonIcon;
  @Input() disabled = false;
  @Input() color: 'blue' | 'white' | 'clearblue' = 'white';

  menuOpened = false;

  openMenu(): void {
    this.menuOpened = !this.menuOpened;
  }
}
