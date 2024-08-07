import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { IconTextButtonComponent } from '../../shared/components/buttons/icon-text-button/icon-text-button.component';
import { DropdownCheckboxComponent } from '../../shared/components/form/dropdown-checkbox/dropdown-checkbox.component';
import { IconButtonComponent } from '../../shared/components/buttons/icon-button/icon-button.component';
import { TableComponent } from '../../shared/components/table/table.component';
import { PageSelectorComponent } from '../../shared/components/page-selector/page-selector.component';
import { InputComponent } from '../../shared/components/form/input/input.component';
import { UserApiService } from '../../services/user-api.service';
import { User } from '../../models/User';
import { FormControl } from '@angular/forms';
import { AddUserDialogComponent } from '../add-user-dialog/add-user-dialog.component';
import { Router } from '@angular/router';
import { PermissionDirective } from '../../shared/directives/permission.directive';
import { SelectComponent } from '../../shared/components/form/select/select.component';
import { RoleApiService } from '../../services/role-api.service';
import { lastValueFrom } from 'rxjs';
import { ConfirmationDialogService } from '../../shared/components/confirmation-dialog/confirmation-dialog.service';

@Component({
  selector: 'app-user-list',
  standalone: true,
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css',
  imports: [
    IconTextButtonComponent,
    DropdownCheckboxComponent,
    IconButtonComponent,
    InputComponent,
    TableComponent,
    PageSelectorComponent,
    AddUserDialogComponent,
    PermissionDirective,
    SelectComponent,
  ],
})
export class UserListComponent implements OnInit {
  numberOfElements!: number;

  itemsPerPage = 10;

  currentPage = 1;

  isDialogOpen = false;

  firstnameSearched = new FormControl('');
  lastnameSearched = new FormControl('');
  statutSearched = new FormControl<string>('');
  functionSearched = new FormControl('');
  emailSearched = new FormControl('');
  roleSearched = new FormControl('');

  users!: User[];

  functions!: string[];
  roles!: string[];

  selectedUsers: Set<number> = new Set<number>();

  isExtendedSearch = false;

  constructor(
    private api: UserApiService,
    private roleapi: RoleApiService,
    private router: Router,
    private confirmationDialog: ConfirmationDialogService
  ) {}

  ngOnInit(): void {
    this.api.getFunctions().subscribe((functions: string[]) => {
      this.functions = functions;
    });
    this.roleapi.getActiveRoles().subscribe((roles) => {
      this.roles = roles;
    });
    this.refreshUserList();
  }

  refreshUserList() {
    this.api.getNumberOfUser().subscribe((count: number) => {
      this.numberOfElements = count;
    });
    this.api
      .getUsers((this.currentPage - 1) * this.itemsPerPage, this.itemsPerPage, {
        firstname: this.valueIfPresent(this.firstnameSearched.value),
        lastname: this.valueIfPresent(this.lastnameSearched.value),
        function: this.valueIfPresent(this.functionSearched.value),
        email: this.valueIfPresent(this.emailSearched.value),
        role: this.valueIfPresent(this.roleSearched.value),
        statut: this.toStatut(this.statutSearched.value),
        password: undefined,
      })
      .subscribe((users: User[]) => {
        this.users = users;
      });
  }

  usersSelectedList($event: Set<number>) {
    this.selectedUsers = $event;
  }

  deleteUsers() {
    this.confirmationDialog.openConfirmationDialog(
      'Confirmer la suppression',
      'Voulez-vous vraiment supprimer ' + (this.selectedUsers.size > 1 ? 'ces utilisateurs ?' : 'cet utilisateur ?')
    ).then((confirmation)=> {
      if (confirmation) {
        Promise.all(
          Array.from(this.selectedUsers).map((id) => lastValueFrom(this.api.deleteUser(this.users[id].id!))),
        ).then(() => {
          this.refreshUserList();
          this.selectedUsers.clear();
          this.api.getFunctions().subscribe((functions: string[]) => {
            this.functions = functions;
          });
          this.roleapi.getActiveRoles().subscribe((roles) => {
            this.roles = roles;
          });
        });
      }
    })
  }

  onPageChange($event: number) {
    this.currentPage = $event;
    this.refreshUserList();
  }

  closeDialog() {
    this.isDialogOpen = false;
  }

  clickedRow($event: number) {
    this.router.navigate(['app', 'user', this.users[$event].id]);
  }

  private valueIfPresent(value: string | null): string | undefined {
    return value ? (value.length > 0 ? value : undefined) : undefined;
  }

  private toStatut(value: string | null): 'active' | 'inactive' | undefined {
    if (value == 'Actif') {
      return 'active';
    } else if (value == 'Inactif') {
      return 'inactive';
    } else {
      return undefined;
    }
  }
}
