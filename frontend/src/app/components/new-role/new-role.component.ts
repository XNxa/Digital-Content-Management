import { Component } from '@angular/core';
import { IconTextButtonComponent } from "../../shared/components/buttons/icon-text-button/icon-text-button.component";
import { InputComponent } from "../../shared/components/form/input/input.component";
import { ToggleButtonComponent } from "../../shared/components/buttons/toggle-button/toggle-button.component";
import { LongInputComponent } from "../../shared/components/form/long-input/long-input.component";
import { PermissionsTreeComponent } from "../permissions-tree/permissions-tree.component";
import { FormControl } from '@angular/forms';
import { RouterModule } from '@angular/router';

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
    roleName = new FormControl('');
    roleState = false;
    roleDescription = new FormControl('');

    save() {
        throw new Error('Method not implemented.');
    }
    cancel() {
        throw new Error('Method not implemented.');
    }
}
