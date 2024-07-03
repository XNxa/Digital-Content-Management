import { Component, Input } from '@angular/core';
import { AbstractControl, FormControl } from '@angular/forms';

@Component({
  selector: 'app-error-message',
  standalone: true,
  imports: [],
  templateUrl: './error-message.component.html',
  styleUrl: './error-message.component.css'
})
export class ErrorMessageComponent {
  @Input() form!: AbstractControl;

  getErrorMessage(form: AbstractControl): string {
    if (form.hasError('required')) {
      return 'Ce champ est obligatoire';
    } else if (form.hasError('email')) {
      return 'Adresse email invalide';
    } else if (form.hasError('maxlength')) {
      return `Ce champ ne doit pas dépasser ${form.errors?.["maxlength"].requiredLength} caractères`;
    } else if (form.hasError('minlength')) {
      return `Ce champ doit contenir au moins ${form.errors?.["minlength"].requiredLength} caractères`;
    } else if (form.hasError('passwordMismatch')) {
      return 'Les mots de passe ne correspondent pas';
    } else if (form.hasError('pattern')) {
      return 'Format invalide';
    } else if (form.hasError('min')) {
      return `La valeur minimale est ${form.errors?.["min"].min}`;
    } else if (form.hasError('max')) {
      return `La valeur maximale est ${form.errors?.["max"].max}`;
    } else if (form.hasError('invalid')) {
      return 'Valeur invalide';
    }
    return '';
  }
}
