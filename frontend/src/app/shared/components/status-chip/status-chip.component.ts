import { Component, Input } from '@angular/core';
import { Status } from '../../../enums/status';

@Component({
  selector: 'app-status-chip',
  standalone: true,
  imports: [],
  templateUrl: './status-chip.component.html',
  styleUrl: './status-chip.component.css',
})
export class StatusChipComponent {
  @Input() status: Status = Status.ARCHIVE;
  @Input() size: 'small' | 'large' = 'small';

  getClass(status: Status): string {
    return status.toLowerCase() + ' ' + this.size;
  }

  getLabel(status: Status): string {
    return Status.printableString(status);
  }
}
