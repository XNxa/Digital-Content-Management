import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  AsyncValidatorFn,
  FormControl,
  FormGroup,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { RoleApiService } from '../../services/role-api.service';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { IconTextButtonComponent } from '../../shared/components/buttons/icon-text-button/icon-text-button.component';
import { InputComponent } from '../../shared/components/form/input/input.component';
import { ToggleButtonComponent } from '../../shared/components/buttons/toggle-button/toggle-button.component';
import { LongInputComponent } from '../../shared/components/form/long-input/long-input.component';
import { PermissionsTreeComponent } from '../permissions-tree/permissions-tree.component';
import { Role } from '../../models/Role';
import { PermissionDirective } from '../../shared/directives/permission.directive';
import { Observable, of, map, catchError, lastValueFrom } from 'rxjs';
import { SnackbarService } from '../../shared/components/snackbar/snackbar.service';

@Component({
  selector: 'app-modify-role',
  standalone: true,
  templateUrl: './modify-role.component.html',
  styleUrl: './modify-role.component.css',
  imports: [
    IconTextButtonComponent,
    InputComponent,
    ToggleButtonComponent,
    LongInputComponent,
    PermissionsTreeComponent,
    RouterModule,
    PermissionDirective,
  ],
})
export class ModifyRoleComponent implements OnInit {
  role!: Role;

  mode: 'consult' | 'modify' = 'consult';

  initialPermissions: Set<string> | undefined = undefined;

  roleName = new FormControl('', {
    validators: Validators.required,
    asyncValidators: this.roleNameValidator(),
    updateOn: 'blur',
  });
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
    private route: ActivatedRoute,
    private snackbar: SnackbarService,
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      this.api.getRole(params['id']).subscribe((role) => {
        this.role = role;
        this.roleName.setValue(role.name);
        this.roleState = role.state;
        this.roleDescription.setValue(role.description);
        this.initialPermissions = new Set(role.permissions);
        this.rolePermissions = new Set(role.permissions);
      });
    });
  }

  save() {
    const deactivatable = !this.roleState
      ? lastValueFrom(this.api.isDeactivatable(this.role.id!))
      : Promise.resolve(true);
    if (this.group.valid) {
      deactivatable.then((isDeactivatable) => {
        if (isDeactivatable) {
          this.api
            .updateRole({
              id: this.role.id!,
              name: this.roleName.value!,
              state: this.roleState,
              description: this.roleDescription.value!,
              permissions: Array.from(this.rolePermissions),
            })
            .subscribe(() => {
              this.mode = 'consult';
              this.initialPermissions = this.rolePermissions;
              this.role.permissions = Array.from(this.rolePermissions);
              this.role.name = this.roleName.value!;
              this.role.state = this.roleState;
              this.role.description = this.roleDescription.value!;
            });
        } else {
          this.snackbar.show(
            'This role cannot be deactivated, it is being used by some users',
          );
        }
      });
    }
  }

  permissionsSelected(permissions: Set<string>): void {
    this.rolePermissions = permissions;
  }

  cancel() {
    this.roleName.setValue(this.role.name);
    this.roleName.markAsUntouched();
    this.roleDescription.setValue(this.role.description);
    this.roleState = this.role.state;
    this.rolePermissions = this.initialPermissions!;
    this.mode = 'consult';
  }

  modify() {
    this.mode = 'modify';
  }

  deleteRole() {
    this.api.isDeactivatable(this.role.id!).subscribe((isDeactivatable) => {
      if (isDeactivatable) {
        this.api.deleteRole(this.role.id!).subscribe(() => {
          this.router.navigate(['/roles']);
        });
      } else {
        this.snackbar.show(
          'This role cannot be deleted, it is being used by some users',
        );
      }
    });
  }

  roleNameValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      if (!control.value) {
        return of(null);
      }

      return this.api.validateRoleName(control.value).pipe(
        map((isValid) =>
          isValid || control.value == this.role.name ? null : { unique: true },
        ),
        catchError(() => of(null)), // Treat as valid if there's an issue with the request
      );
    };
  }
}
