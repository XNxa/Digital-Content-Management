import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { ChipsInputComponent } from '../../shared/components/form/chips-input/chips-input.component';
import { SelectComponent } from '../../shared/components/form/select/select.component';
import { IconTextButtonComponent } from '../../shared/components/buttons/icon-text-button/icon-text-button.component';
import { InputComponent } from '../../shared/components/form/input/input.component';
import { ToggleButtonComponent } from '../../shared/components/buttons/toggle-button/toggle-button.component';
import { ErrorMessageComponent } from '../../shared/components/form/error-message/error-message.component';
import { User } from '../../models/User';
import { UserApiService } from '../../services/user-api.service';
import { RoleApiService } from '../../services/role-api.service';

@Component({
  selector: 'app-add-user-dialog',
  standalone: true,
  imports: [InputComponent, SelectComponent, ChipsInputComponent, IconTextButtonComponent, ToggleButtonComponent, ReactiveFormsModule, ErrorMessageComponent],
  templateUrl: './add-user-dialog.component.html',
  styleUrl: './add-user-dialog.component.css'
})
export class AddUserDialogComponent implements OnInit {
  title = "Ajouter un nouvel utilisateur";

  @Output() close = new EventEmitter<void>();

  currentStep = 1;

  firstname = new FormControl('', [Validators.required, Validators.maxLength(255)]);
  lastname = new FormControl('', [Validators.required, Validators.maxLength(255)]);
  function = new FormControl('', [Validators.required, Validators.maxLength(255)]);
  email = new FormControl('', [Validators.required, Validators.email, Validators.maxLength(255)]);

  role = new FormControl('', [Validators.required]);
  statut = false;

  password = new FormControl('', [Validators.required, Validators.minLength(8)]);
  passwordConfirmation = new FormControl('', [Validators.required, Validators.minLength(8)]);

  group1: FormGroup;
  group2: FormGroup;
  group3: FormGroup;

  roleOptions: string[] = [];

  constructor(private roleapi : RoleApiService, private userapi: UserApiService) {
    this.group1 = new FormGroup({ firstname: this.firstname, lastname: this.lastname, function: this.function, email: this.email });
    this.group2 = new FormGroup({ role: this.role });
    this.group3 = new FormGroup({ password: this.password, passwordConfirmation: this.passwordConfirmation }, {
      validators: this.passwordsMatchValidator()
    });
  }

  ngOnInit(): void {
    this.roleapi.getActiveRoles().subscribe(roles => {
      this.roleOptions = roles;
    });
  }

  passwordsMatchValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const password = control.get('password');
      const passwordConfirmation = control.get('passwordConfirmation');

      if (password && passwordConfirmation && password.value != passwordConfirmation.value) {
        return { passwordMismatch: true };
      }
      return null;
    };
  }

  previousStep() {
    if (this.currentStep > 1) {
      this.currentStep--;
    }
  }

  nextStep() {
    if (this.currentStep == 1) {
      this.group1.markAllAsTouched();
      if (this.group1.valid) {
        this.currentStep++;
      }
    } else if (this.currentStep == 2) {
      this.group2.markAllAsTouched();
      if (this.group2.valid) {
        this.currentStep++;
      }
    }
  }

  cancel() {
    this.close.emit();
  }

  save() {
    this.group3.markAllAsTouched();
    if (this.group3.valid) {
      const user: User = {
        id: undefined,
        firstname: this.firstname.value!,
        lastname: this.lastname.value!,
        function: this.function.value!,
        email: this.email.value!,
        role: this.role.value!,
        statut: this.statut ? 'active' : 'inactive',
        password: this.password.value!,
      };
      this.userapi.createUser(user).subscribe(() => {
        this.close.emit();
      });
    }
  }

}