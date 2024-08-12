import { Component, OnInit } from '@angular/core';
import { TableComponent } from '../../shared/components/table/table.component';
import { PageSelectorComponent } from '../../shared/components/page-selector/page-selector.component';
import { IconButtonComponent } from '../../shared/components/buttons/icon-button/icon-button.component';
import { IconTextButtonComponent } from '../../shared/components/buttons/icon-text-button/icon-text-button.component';
import { Role } from '../../models/Role';
import { RoleApiService } from '../../services/role-api.service';
import { SelectComponent } from '../../shared/components/form/select/select.component';
import { FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { PermissionDirective } from '../../shared/directives/permission.directive';
import { InputComponent } from '../../shared/components/form/input/input.component';

@Component({
  selector: 'app-role-list',
  standalone: true,
  templateUrl: './role-list.component.html',
  styleUrl: './role-list.component.css',
  imports: [
    TableComponent,
    PageSelectorComponent,
    IconButtonComponent,
    IconTextButtonComponent,
    SelectComponent,
    PermissionDirective,
    InputComponent,
  ],
})
export class RoleListComponent implements OnInit {
  selectedRoles: Set<number> = new Set<number>();

  roles!: Role[];

  itemsPerPage = 5;
  numberOfElements!: number;
  currentPage = 1;
  statutOptions = ['', 'Actif', 'Inactif'];
  selectedStatut = new FormControl('');
  searchRoleName = new FormControl('');

  constructor(
    private api: RoleApiService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.refreshRoleList();
  }

  refreshRoleList() {
    this.api.countRoles().subscribe((count) => {
      this.numberOfElements = count;
    });
    this.api
      .getRoles((this.currentPage - 1) * this.itemsPerPage, this.itemsPerPage, {
        state: this.toState(this.selectedStatut.value),
        name: this.valueIfPresent(this.searchRoleName.value),
      } as Role)
      .subscribe((roles) => {
        this.roles = roles.filter((r) =>
          this.toState(this.selectedStatut.value) != null
            ? r.state == this.toState(this.selectedStatut.value)
            : true,
        );
      });
  }

  roleSelectedList($event: Set<number>) {
    this.selectedRoles = $event;
  }

  addNewRole() {
    this.router.navigate(['app', 'roles', 'add']);
  }

  onPageChange($event: number) {
    this.currentPage = $event;
    this.refreshRoleList();
  }

  modifyRole($event: number) {
    this.router.navigate(['app', 'role', this.roles[$event].id]);
  }

  private toState(value: string | null): boolean | undefined {
    if (value === 'Actif') {
      return true;
    } else if (value === 'Inactif') {
      return false;
    } else {
      return undefined;
    }
  }

  private valueIfPresent(value: string | null): string | undefined {
    return value ? (value.length > 0 ? value : undefined) : undefined;
  }
}
