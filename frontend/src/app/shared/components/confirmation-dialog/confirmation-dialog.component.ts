import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { IconTextButtonComponent } from "../buttons/icon-text-button/icon-text-button.component";

@Component({
  selector: 'app-confirmation-dialog',
  standalone: true,
  imports: [IconTextButtonComponent],
  templateUrl: './confirmation-dialog.component.html',
  styleUrl: './confirmation-dialog.component.css',
})
export class ConfirmationDialogComponent {
  @Input() title!: string;
  @Input() text!: string;
  @Output() confirmed = new EventEmitter<boolean>();

  constructor(private changedetector : ChangeDetectorRef) {}

  public init() {
    this.changedetector.detectChanges();
  }

  confirm() {
    this.confirmed.emit(true);
  }

  cancel() {
    this.confirmed.emit(false);
  }
}
