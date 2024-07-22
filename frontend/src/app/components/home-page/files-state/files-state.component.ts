import { Component } from '@angular/core';
import { FileApiService } from '../../../services/file-api.service';

@Component({
  selector: 'app-files-state',
  standalone: true,
  imports: [],
  templateUrl: './files-state.component.html',
  styleUrl: './files-state.component.css',
})
export class FilesStateComponent {
  statuses = [
    { name: 'Planifié', value: 1, color: 'color-planned' },
    { name: 'Publié', value: 0, color: 'color-published' },
    { name: 'En attente', value: 0, color: 'color-pending' },
    { name: 'Non publié', value: 0, color: 'color-unpublished' },
    { name: 'Archivé', value: 0, color: 'color-archived' },
    { name: 'Erreur de publication', value: 0, color: 'color-error' },
  ];

  total = this.statuses.reduce((sum, status) => sum + status.value, 0);

  constructor(private api: FileApiService) {}

  ngOnInit(): void {
    this.api.getStatuses().subscribe((statuses) => {
      for (let index = 0; index < statuses.length; index++) {
        this.statuses[index]['value'] = statuses[index];
      }
      this.total = this.statuses.reduce((sum, status) => sum + status.value, 0);
      // Update the view
    });
  }

  getStrokeDasharray(value: number): string {
    const percentage = (value / this.total) * 100;
    const circumference = 2 * Math.PI * 15;
    const dasharray = (percentage / 100) * circumference;
    return `${dasharray} ${circumference - dasharray}`;
  }

  getStrokeDashoffset(index: number): number {
    const circumference = 2 * Math.PI * 15;
    let offset = circumference * 0.25;
    for (let i = 0; i < index; i++) {
      offset -= (this.statuses[i].value / this.total) * circumference;
    }
    return offset;
  }
}
