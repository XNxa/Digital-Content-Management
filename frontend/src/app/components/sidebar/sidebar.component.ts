import { Component } from '@angular/core';
import { CdkTreeModule, FlatTreeControl } from '@angular/cdk/tree';
import { UserCardFooterComponent } from '../../shared/components/user-card-footer/user-card-footer.component';
import { TREE, Node, getRouteForNode } from '../../models/Tabs';
import { Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';
import { UserApiService } from '../../services/user-api.service';
import { User, UserFilter } from '../../models/User';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CdkTreeModule, UserCardFooterComponent],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent {
  treeControl = new FlatTreeControl<Node>(
    (node) => node.level,
    (node) => node.expandable,
  );

  dataSource = TREE.map((node) => {
    if (node.name == 'Accueil') {
      node.isSelected = true;
      this.router.navigate([getRouteForNode(node)]);
    }
    return node;
  });

  currentUser!: User;

  constructor(private router: Router, private auth: KeycloakService, private userapi: UserApiService) {}

  ngOnInit() {
    const filter : UserFilter = {
      email: this.auth.getUsername(),
      firstname: undefined,
      lastname: undefined,
      function: undefined,
      role: undefined,
      statut: undefined,
      password: undefined
    };
    this.userapi.getUsers(0, 1, filter).subscribe((user) => {
      if (user.length === 1) {
        this.currentUser = user[0];
      }
    });
  }

  hasChild = (_: number, node: Node) => node.expandable;

  getParentNode(node: Node): Node | null {
    const nodeIndex = this.dataSource.indexOf(node);
    for (let i = nodeIndex - 1; i >= 0; i--) {
      if (this.dataSource[i].level === node.level - 1) {
        return this.dataSource[i];
      }
    }
    return null;
  }

  shouldRender(node: Node): boolean {
    let parent = this.getParentNode(node);
    while (parent) {
      if (!parent.isExpanded) {
        return false;
      }
      parent = this.getParentNode(parent);
    }
    return true;
  }

  getLogoPath(node: Node): string {
    return `icons/sidebar/${node.isSelected ? 'blue' : 'gray'}/${node.logo}.svg`;
  }

  getArrowPath(node: Node): string {
    return `icons/sidebar/${node.isSelected ? 'blue' : 'gray'}/arrow-${node.isExpanded ? 'open' : 'close'}.svg`;
  }

  selectNode(node: Node): void {
    if (node.expandable) {
      this.dataSource.forEach((n) => {
        if (n.name != node.name) n.isExpanded = false;
      });
      node.isExpanded = !node.isExpanded;
    } else {
      this.dataSource.forEach((n) => (n.isSelected = false));
      node.isSelected = true;
    }
    this.navigateTo(node);
  }

  getClass(node: Node): string {
    if (node.title) {
      return 'title';
    } else {
      let type = node.expandable ? 'expandable' : 'leaf';
      if (type == 'leaf' && !this.getParentNode(node)) {
        type += ' no-parent';
      }
      return type + (node.isSelected ? ' selected' : '');
    }
  }

  isTitle(_: number, node: Node): boolean {
    return node.title || false;
  }

  navigateTo(node: Node): void {
    const parent = this.getParentNode(node);
    if (parent) {
      this.router.navigate([getRouteForNode(parent), getRouteForNode(node)]);
    } else {
      if (!node.expandable) this.router.navigate([getRouteForNode(node)]);
    }
  }

  openProfile() {
    this.router.navigate(['profile', this.currentUser.id]);
    this.dataSource.forEach((n) => (n.isSelected = false));
  }

  signout() {
    this.auth.logout();
  }
}
