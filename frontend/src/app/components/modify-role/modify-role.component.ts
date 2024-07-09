import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { RoleApiService } from '../../services/role-api.service';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { IconTextButtonComponent } from "../../shared/components/buttons/icon-text-button/icon-text-button.component";
import { InputComponent } from "../../shared/components/form/input/input.component";
import { ToggleButtonComponent } from "../../shared/components/buttons/toggle-button/toggle-button.component";
import { LongInputComponent } from "../../shared/components/form/long-input/long-input.component";
import { PermissionsTreeComponent } from "../permissions-tree/permissions-tree.component";
import { Role } from '../../models/Role';

@Component({
  selector: 'app-modify-role',
  standalone: true,
  templateUrl: './modify-role.component.html',
  styleUrl: './modify-role.component.css',
  imports: [IconTextButtonComponent, InputComponent, ToggleButtonComponent, LongInputComponent, PermissionsTreeComponent, RouterModule]
})
export class ModifyRoleComponent implements OnInit {

  role!: Role;

  mode: 'consult' | 'modify' = 'consult';

  initialPermissions: Set<string> | undefined = undefined;

  roleName = new FormControl('', Validators.required);
  roleState = false;
  roleDescription = new FormControl('', Validators.maxLength(255));
  rolePermissions = new Set<string>();

  group: FormGroup = new FormGroup({ roleName: this.roleName, roleDescription: this.roleDescription });

  constructor(private api: RoleApiService, private router: Router, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.api.getRole(params['id']).subscribe(role => {
        this.role = role;
        this.roleName.setValue(role.name);
        this.roleState = role.state;
        this.roleDescription.setValue(role.description);
        this.initialPermissions = new Set(role.permissions);
      });
    });
  }

  save() {
    if (this.group.valid) {
      this.api.updateRole({
        id: this.role.id!,
        name: this.roleName.value!,
        state: this.roleState,
        description: this.roleDescription.value!,
        permissions: Array.from(this.rolePermissions),
      }).subscribe(() => {
        this.mode = 'consult';
        this.initialPermissions = this.rolePermissions;
        this.role.permissions = Array.from(this.rolePermissions);
        this.role.name = this.roleName.value!;
        this.role.state = this.roleState;
        this.role.description = this.roleDescription.value!;
      });
    }
  }

  permissionsSelected(permissions: Set<string>): void {
    this.rolePermissions = permissions
  }

  cancel() {
    this.roleName.setValue(this.role.name);
    this.roleDescription.setValue(this.role.description);
    this.roleState = this.role.state;
    this.rolePermissions = this.initialPermissions!;
    this.mode = 'consult';
  }

  modify() {
    this.mode = 'modify';
  }

  deleteRole() {
    this.api.deleteRole(this.role.id!).subscribe(() => {
      this.router.navigate(['/roles']);
    });
  }
}
