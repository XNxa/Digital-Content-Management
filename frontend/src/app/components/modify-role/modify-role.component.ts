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
        this.roleName.setValue(role.name);
        this.roleState = role.state;
        this.roleDescription.setValue(role.description);
        this.initialPermissions = new Set(role.permissions);
      });
    });
  }

  save() {
    console.log(this.group.valid);
    if (this.group.valid) {
      this.api.createRole({
        id: undefined,
        name: this.roleName.value!,
        state: this.roleState,
        description: this.roleDescription.value!,
        permissions: Array.from(this.rolePermissions),
      }).subscribe(() => {
        this.router.navigate(['/roles']);
      });
    }
  }

  permissionsSelected(permissions: Set<string>): void {
    this.rolePermissions = permissions
  }

  cancel() {
    throw new Error('Method not implemented.');
  }

  modify() {
    throw new Error('Method not implemented.');
  }

  deleteRole() {
    throw new Error('Method not implemented.');
  }
}
