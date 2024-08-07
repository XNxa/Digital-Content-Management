import { Component } from '@angular/core';
import { IconTextButtonComponent } from '../../shared/components/buttons/icon-text-button/icon-text-button.component';
import { InputComponent } from '../../shared/components/form/input/input.component';
import { SelectComponent } from '../../shared/components/form/select/select.component';
import { ToggleButtonComponent } from '../../shared/components/buttons/toggle-button/toggle-button.component';
import { User } from '../../models/User';
import {
  AbstractControl,
  AsyncValidatorFn,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { UserApiService } from '../../services/user-api.service';
import { RoleApiService } from '../../services/role-api.service';
import { ActivatedRoute, Router } from '@angular/router';
import { PermissionDirective } from '../../shared/directives/permission.directive';
import { Observable, catchError, map, of } from 'rxjs';
import { ConfirmationDialogService } from '../../shared/components/confirmation-dialog/confirmation-dialog.service';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [
    IconTextButtonComponent,
    InputComponent,
    SelectComponent,
    ToggleButtonComponent,
    PermissionDirective,
  ],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css',
})
export class UserProfileComponent {
  mode: 'modify' | 'consult' = 'consult';

  user!: User;

  firstname = new FormControl('', [Validators.required]);
  lastname = new FormControl('', [Validators.required]);
  function = new FormControl('', [Validators.required]);
  email = new FormControl('', [Validators.required, Validators.email]);

  role = new FormControl('', [Validators.required]);
  state = false;

  oldPassword = new FormControl('', {
    validators: Validators.required,
    asyncValidators: this.crendentialsValidator(),
    updateOn: 'blur',
  });
  newPassword = new FormControl('', [Validators.required]);
  passwordConfirmation = new FormControl('', [Validators.required]);

  group1 = new FormGroup({
    firstname: this.firstname,
    lastname: this.lastname,
    function: this.function,
    email: this.email,
  });
  group2 = new FormGroup({ role: this.role });
  group3 = new FormGroup(
    {
      password: this.newPassword,
      passwordConfirmation: this.passwordConfirmation,
    },
    {
      validators: this.passwordsMatchValidator(),
    },
  );

  roleOptions!: string[];

  constructor(
    private userapi: UserApiService,
    private roleapi: RoleApiService,
    private route: ActivatedRoute,
    private router: Router,
    private confirmationDialog: ConfirmationDialogService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.userapi.getUser(params['id']).subscribe((user) => {
        this.user = user;
        this.firstname.setValue(user.firstname);
        this.lastname.setValue(user.lastname);
        this.function.setValue(user.function);
        this.email.setValue(user.email);
        this.role.setValue(user.role);
        this.state = user.statut == 'active';
      });
    });
    this.roleapi.getActiveRoles().subscribe((roles) => {
      this.roleOptions = roles;
    });
  }

  passwordsMatchValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const password = control.get('password');
      const passwordConfirmation = control.get('passwordConfirmation');

      if (
        password &&
        passwordConfirmation &&
        password.value != passwordConfirmation.value
      ) {
        return { passwordMismatch: true };
      }
      return null;
    };
  }

  crendentialsValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value) {
        return of(null);
      }

      return this.userapi
        .validateCredentials(this.user.email, control.value)
        .pipe(
          map((isValid) => (isValid ? null : { wrongPassword: true })),
          catchError(() => of({ wrongPassword: true })), // Treat as invalid if there's an issue with the request
        );
    };
  }

  deleteUser() {
    this.confirmationDialog.openConfirmationDialog(
      'Confirmer la suppression',
      'Voulez-vous vraiment supprimer cet utilisateur ?'
    ).then((confirmation) => {
      if (confirmation) {
        this.userapi.deleteUser(this.user.id!).subscribe(() => {
          this.router.navigate(['app', '/user']);
        });
      }
    })
  }

  modify() {
    this.mode = 'modify';
  }

  cancel() {
    this.firstname.setValue(this.user.firstname);
    this.lastname.setValue(this.user.lastname);
    this.function.setValue(this.user.function);
    this.email.setValue(this.user.email);
    this.role.setValue(this.user.role);
    this.state = this.user.statut == 'active';
    this.mode = 'consult';
  }

  save() {
    if (this.group1.valid && this.group2.valid && this.group3.valid) {
      const user: User = {
        id: this.user.id,
        firstname: this.firstname.value!,
        lastname: this.lastname.value!,
        function: this.function.value!,
        email: this.email.value!,
        role: this.role.value!,
        statut: this.state ? 'active' : 'inactive',
        password: this.newPassword.value!,
      };

      this.userapi.updateUser(user).subscribe(() => {
        this.mode = 'consult';
      });
    } else {
      this.group1.markAllAsTouched();
      this.group2.markAllAsTouched();
      this.group3.markAllAsTouched();
    }
  }
}
