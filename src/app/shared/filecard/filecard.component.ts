import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-filecard',
  standalone: true,
  imports: [],
  templateUrl: './filecard.component.html',
  styleUrl: './filecard.component.css'
})
export class FilecardComponent {

  @Input() filename = 'Name';
  @Input() filetype = '.TYP';
  @Input() filesize = '48 Mo';
  @Input() filedate = '25/01/2000';

}
