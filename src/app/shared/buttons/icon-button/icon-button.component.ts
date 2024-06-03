import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-icon-button',
  standalone: true,
  imports: [],
  templateUrl: './icon-button.component.html',
  styleUrl: './icon-button.component.css'
})
export class IconButtonComponent {
log() {
console.log(this.getIconPath());
}

  @Input() title = '';
  @Input() disabled = false;

  @Input() icon!: 
  'move' |
  'delete' |
  'menu' |
  'modify' |
  'card' |
  'list' |
  'update' |
  'close' |
  'back';

  getIconPath() {
    return 'icons/' + this.icon + '.svg';
  }
}
