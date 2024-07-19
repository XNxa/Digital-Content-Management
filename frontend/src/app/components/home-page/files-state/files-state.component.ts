import { Component } from '@angular/core';

@Component({
  selector: 'app-files-state',
  standalone: true,
  imports: [],
  templateUrl: './files-state.component.html',
  styleUrl: './files-state.component.css',
})
export class FilesStateComponent {
  statuses = [
    { name: 'Planifié', value: 40, color: 'color-planned' },
    { name: 'Publié', value: 20, color: 'color-published' },
    { name: 'En attente', value: 20, color: 'color-pending' },
    { name: 'Non publié', value: 40, color: 'color-unpublished' },
    { name: 'Archivé', value: 40, color: 'color-archived' },
    { name: 'Erreur de publication', value: 60, color: 'color-error' },
  ];

  total = this.statuses.reduce((sum, status) => sum + status.value, 0);

  constructor() {}

  ngOnInit(): void {}

  getStrokeDasharray(value: number): string {
    const percentage = (value / this.total) * 100;
    return `${percentage} ${100 - percentage}`;
  }

  getStrokeDashoffset(index: number): number {
    let offset = 0; // Start offset
    for (let i = 0; i < index; i++) {
      offset -= (this.statuses[i].value / this.total) * 100;
    }
    return offset;
  }
}
