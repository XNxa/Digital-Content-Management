import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-user-card-footer',
  standalone: true,
  imports: [],
  templateUrl: './user-card-footer.component.html',
  styleUrl: './user-card-footer.component.css',
})
export class UserCardFooterComponent {
  @Input() name!: string;
  @Input() pp = '';
  @Output() openProfile = new EventEmitter<boolean>();
  @Output() signout = new EventEmitter<boolean>();

  menuOpen = false;

  open() {
    this.menuOpen = !this.menuOpen;
  }

  signoutClick() {
    this.menuOpen = false;
    this.signout.emit(true);
  }
  profileClick() {
    this.menuOpen = false;
    this.openProfile.emit(true);
  }
}
