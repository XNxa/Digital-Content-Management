import { Component, Input } from '@angular/core';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-small-card',
  standalone: true,
  imports: [],
  templateUrl: './small-card.component.html',
  styleUrl: './small-card.component.css',
})
export class SmallCardComponent {
  @Input() valueFunction!: () => Observable<string | number>;
  @Input() icon!: 'image' | 'video' | 'picto' | 'doc';
  @Input() description!: string;

  value: string | number = '';

  ngOnInit() {
    this.valueFunction().subscribe((value) => {
      this.value = value;
    });
  }
}
