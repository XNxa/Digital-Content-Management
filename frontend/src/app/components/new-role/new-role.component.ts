import { Component } from '@angular/core';
import { IconTextButtonComponent } from "../../shared/components/buttons/icon-text-button/icon-text-button.component";
import { InputComponent } from "../../shared/components/form/input/input.component";
import { ToggleButtonComponent } from "../../shared/components/buttons/toggle-button/toggle-button.component";
import { LongInputComponent } from "../../shared/components/form/long-input/long-input.component";
import { PermissionsTreeComponent } from "../permissions-tree/permissions-tree.component";
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { RoleApiService } from '../../services/role-api.service';
import { routes } from '../../app.routes';

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
        PermissionsTreeComponent
    ]
})
export class NewRoleComponent {
    roleName = new FormControl('', Validators.required);
    roleState = false;
    roleDescription = new FormControl('', Validators.maxLength(255));
    rolePermissions = new Set<string>();

    group: FormGroup = new FormGroup({ roleName: this.roleName, roleDescription: this.roleDescription });

    constructor(private api: RoleApiService, private router: Router) { }

    save() {
        if (this.group.valid) {
            this.api.createRole({
                id: undefined,
                name: this.roleName.value!,
                state: this.roleState,
                description: this.roleDescription.value!,
                permissions: Array.from(this.rolePermissions)
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
}
