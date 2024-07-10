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
import { FormControl } from '@angular/forms';
import { AddUserDialogComponent } from '../add-user-dialog/add-user-dialog.component';
import { Router } from '@angular/router';
import { PermissionDirective } from '../../shared/directives/permission.directive';

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
        AddUserDialogComponent,
        PermissionDirective
    ]
})
export class UserListComponent implements OnInit {
    numberOfElements!: number;

    itemsPerPage: number = 10;

    currentPage: number = 1;

    isDialogOpen: boolean = false;

    firstnameSearched = new FormControl('');
    lastnameSearched = new FormControl('');

    users!: User[];

    selectedUsers: Set<number> = new Set<number>();

    constructor(private api: UserApiService, private router: Router) { }

    ngOnInit(): void {
        this.refreshUserList();
    }

    valueIfPresent(value: string | null): string | undefined {
        return value ? (value.length > 0 ? value : undefined) : undefined;
    }

    refreshUserList() {
        this.api.getNumberOfUser().subscribe((count: number) => {
            this.numberOfElements = count;
        });
        this.api.getUsers((this.currentPage - 1) * this.itemsPerPage, this.itemsPerPage, {
            firstname: this.valueIfPresent(this.firstnameSearched.value),
            lastname: this.valueIfPresent(this.lastnameSearched.value),
            function: undefined,
            email: undefined,
            role: undefined,
            statut: undefined,
            password: undefined
        }).subscribe((users: User[]) => {
            this.users = users;
        });
    }

    fileSelectedList($event: Set<number>) {
        throw new Error('Method not implemented.');
    }

    onPageChange($event: number) {
        this.currentPage = $event;
        this.refreshUserList();
    }

    closeDialog() {
        this.isDialogOpen = false;
    }

    clickedRow($event: number) {
        this.router.navigate(['user', this.users[$event].id]);
    }
}
