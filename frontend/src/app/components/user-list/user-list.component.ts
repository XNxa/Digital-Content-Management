import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { IconTextButtonComponent } from "../../shared/components/buttons/icon-text-button/icon-text-button.component";
import { UploadDialogComponent } from "../upload-dialog/upload-dialog.component";
import { DropdownCheckboxComponent } from "../../shared/components/form/dropdown-checkbox/dropdown-checkbox.component";
import { IconButtonComponent } from "../../shared/components/buttons/icon-button/icon-button.component";
import { TableComponent } from "../../shared/components/table/table.component";
import { PageSelectorComponent } from "../../shared/components/page-selector/page-selector.component";
import { InputComponent } from '../../shared/components/form/input/input.component';
import { UserApiService } from '../../services/user-api.service';
import { User } from '../../models/User';

@Component({
    selector: 'app-user-list',
    standalone: true,
    templateUrl: './user-list.component.html',
    styleUrl: './user-list.component.css',
    imports: [
        IconTextButtonComponent,
        UploadDialogComponent,
        DropdownCheckboxComponent,
        IconButtonComponent,
        InputComponent,
        TableComponent,
        PageSelectorComponent,
    ]
})
export class UserListComponent implements OnInit {

    numberOfElements!: number;

    itemsPerPage: number = 10;

    isDialogOpen: boolean = false;

    usernameSearched: string = '';

    users!: User[];

    selectedUsers: Set<number> = new Set<number>();

    constructor(private api: UserApiService) { }

    ngOnInit(): void {
        this.api.getNumberOfUser().subscribe((count: number) => {
            this.numberOfElements = count;
        });
        this.api.getUsers(0, this.itemsPerPage, {
            firstname: undefined,
            lastname: undefined,
            function: undefined,
            email: undefined,
            role: undefined,
            statut: undefined,
            password: undefined
        }).subscribe((users: User[]) => {
            this.users = users;
        });
    }

    refreshUserList() {
        throw new Error('Method not implemented.');
    }

    fileSelectedList($event: Set<number>) {
        throw new Error('Method not implemented.');
    }

    onPageChange($event: number) {
        throw new Error('Method not implemented.');
    }

}
