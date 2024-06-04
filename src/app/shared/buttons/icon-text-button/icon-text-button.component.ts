import { Component, Input } from '@angular/core';
import { ButtonIcon } from '../icon-button-type';

@Component({
  selector: 'app-icon-text-button',
  standalone: true,
  imports: [],
  templateUrl: './icon-text-button.component.html',
  styleUrl: './icon-text-button.component.css'
})
export class IconTextButtonComponent {
  @Input() title = '';
  @Input() disabled = false;

  @Input() icon!: ButtonIcon;

  getIconPath() {
    return 'icons/' + this.icon + '.svg';
  }
}
