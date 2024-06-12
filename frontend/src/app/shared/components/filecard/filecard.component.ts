import { Component, Input } from '@angular/core';
import { FileHeader } from '../../../models/FileHeader';

@Component({
  selector: 'app-filecard',
  standalone: true,
  imports: [],
  templateUrl: './filecard.component.html',
  styleUrl: './filecard.component.css'
})
export class FilecardComponent {
  
  @Input() file : FileHeader | undefined;

  isChecked = false;

  onCardClicked(): void {
    this.isChecked = !this.isChecked;
  }
}
