import { Component, OnInit } from '@angular/core';
import { TableComponent } from "../../shared/components/table/table.component";
import { PageSelectorComponent } from "../../shared/components/page-selector/page-selector.component";
import { IconButtonComponent } from "../../shared/components/buttons/icon-button/icon-button.component";
import { IconTextButtonComponent } from "../../shared/components/buttons/icon-text-button/icon-text-button.component";
import { Role } from '../../models/Role';
import { RoleApiService } from '../../services/role-api.service';
import { SelectComponent } from '../../shared/components/form/select/select.component';
import { FormControl } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-role-list',
  standalone: true,
  templateUrl: './role-list.component.html',
  styleUrl: './role-list.component.css',
  imports: [TableComponent, PageSelectorComponent, IconButtonComponent, IconTextButtonComponent, SelectComponent]
})
export class RoleListComponent implements OnInit {

  selectedRoles: Set<number> = new Set<number>();

  roles!: Role[];

  itemsPerPage: number = 5;
  numberOfElements!: number;
  currentPage: number = 1;
  statutOptions = ["", "Actif", "Inactif"];
  selectedStatut = new FormControl('');

  constructor(private api: RoleApiService, private router: Router) { }

  ngOnInit(): void {
    this.refreshRoleList();
  }

  refreshRoleList() {
    this.api.countRoles().subscribe(count => {
      this.numberOfElements = count;
    });
    this.api.getRoles((this.currentPage - 1) * this.itemsPerPage, this.itemsPerPage, {} as Role).subscribe(roles => {
      this.roles = roles;
    });
  }

  roleSelectedList($event: Set<number>) {
    this.selectedRoles = $event;
  }

  addNewRole() {
    this.router.navigate(['roles/add']);
  }

  onPageChange($event: number) {
    this.currentPage = $event;
    this.refreshRoleList();
  }
}
