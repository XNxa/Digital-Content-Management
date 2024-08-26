import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Status } from '../../../enums/status';

@Component({
  selector: 'app-status-chip',
  standalone: true,
  imports: [],
  templateUrl: './status-chip.component.html',
  styleUrl: './status-chip.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class StatusChipComponent {
  @Input({ transform: toStatus }) status: Status = Status.ARCHIVE;
  @Input() size: 'small' | 'large' = 'small';

  getClass(status: Status): string {
    return status.toLowerCase() + ' ' + this.size;
  }

  getLabel(status: Status): string {
    return Status.printableString(status);
  }
}

function toStatus(value : Status | string): Status {
  if (typeof value === 'string') {
    return Status.fromString(value);
  } else {
    return value;
  }
}
