import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-user-card-footer',
  standalone: true,
  imports: [],
  templateUrl: './user-card-footer.component.html',
  styleUrl: './user-card-footer.component.css'
})
export class UserCardFooterComponent {

  // TODO : Change that
  @Input() name = 'Hassania Mouachi'
  @Input() pp = ''; 
  @Output() openProfile = new EventEmitter<boolean>();
  
  profileOpen = false;
  


}
