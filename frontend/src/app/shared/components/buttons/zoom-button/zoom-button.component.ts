import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-zoom-button',
  standalone: true,
  imports: [],
  templateUrl: './zoom-button.component.html',
  styleUrl: './zoom-button.component.css'
})
export class ZoomButtonComponent {

  zoom = 1;
  @Output() onZoom : EventEmitter<number> = new EventEmitter<number>();

  restoreZoom(): void {
    this.zoom = 1;
    this.onZoom.emit(this.zoom);
  }

  zoomIn(): void {
    if (this.zoom < 2) {
      this.zoom += 0.1;
    }
    this.onZoom.emit(this.zoom);
  }

  zoomOut(): void {
    if (this.zoom > 0.2) {
      this.zoom -= 0.1;
    }
    this.onZoom.emit(this.zoom);
  }

}
