import { Component } from '@angular/core';
import { IconTextButtonComponent } from '../../shared/components/buttons/icon-text-button/icon-text-button.component';
import { InputComponent } from '../../shared/components/form/input/input.component';
import { ToggleButtonComponent } from '../../shared/components/buttons/toggle-button/toggle-button.component';
import { LongInputComponent } from '../../shared/components/form/long-input/long-input.component';
import { PermissionsTreeComponent } from '../permissions-tree/permissions-tree.component';
import {
  AbstractControl,
  AsyncValidatorFn,
  FormControl,
  FormGroup,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { RoleApiService } from '../../services/role-api.service';
import { Observable, catchError, map, of } from 'rxjs';

@Component({
  selector: 'app-new-role',
  standalone: true,
  templateUrl: './new-role.component.html',
  styleUrl: './new-role.component.css',
  imports: [
    RouterModule,
    IconTextButtonComponent,
    InputComponent,
    ToggleButtonComponent,
    LongInputComponent,
    PermissionsTreeComponent,
  ],
})
export class NewRoleComponent {
  roleName = new FormControl('', {validators:Validators.required, asyncValidators:this.roleNameValidator(), updateOn: 'blur'});
  roleState = false;
  roleDescription = new FormControl('', Validators.maxLength(255));
  rolePermissions = new Set<string>();

  group: FormGroup = new FormGroup({
    roleName: this.roleName,
    roleDescription: this.roleDescription,
  });

  constructor(
    private api: RoleApiService,
    private router: Router,
  ) {}

  save() {
    if (this.group.valid) {
      this.api
        .createRole({
          id: undefined,
          name: this.roleName.value!,
          state: this.roleState,
          description: this.roleDescription.value!,
          permissions: Array.from(this.rolePermissions),
        })
        .subscribe(() => {
          this.router.navigate(['/role']);
        });
    }
  }

  permissionsSelected(permissions: Set<string>): void {
    this.rolePermissions = permissions;
  }

  cancel() {
    this.router.navigate(['/role']);
  }

  roleNameValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value) {
        return of(null);
      }

      return this.api.validateRoleName(control.value).pipe(
        map((isValid) => (isValid ? null : { unique: true })),
        catchError(() => of(null)), // Treat as valid if there's an issue with the request
      );
    };
  }
}
