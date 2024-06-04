import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonIcon } from '../icon-button-type';

@Component({
  selector: 'app-icon-button',
  standalone: true,
  imports: [],
  templateUrl: './icon-button.component.html',
  styleUrl: './icon-button.component.css'
})
export class IconButtonComponent {
  @Input() icon!: ButtonIcon;
  @Input() disabled = false;
  @Input() color: 'blue' | 'white' | 'clearblue' = 'blue';
  @Output() click = new EventEmitter();

  getIconPath() {
    return 'icons/' + ((this.color == 'blue')?'white/':'blue/') + this.icon + '.svg';
  }
}
