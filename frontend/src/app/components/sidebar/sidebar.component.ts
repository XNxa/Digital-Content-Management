import { Component } from '@angular/core';
import { CdkTreeModule, FlatTreeControl } from '@angular/cdk/tree';
import { UserCardFooterComponent } from '../../shared/components/user-card-footer/user-card-footer.component';
import { TREE, Node, getRouteForNode } from '../../models/Tabs';
import { Router } from '@angular/router';
import { KeycloakService } from 'keycloak-angular';
import { UserApiService } from '../../services/user-api.service';
import { User, UserFilter } from '../../models/User';
import { PermissionDirective } from '../../shared/directives/permission.directive';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CdkTreeModule, UserCardFooterComponent, PermissionDirective],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent {
  treeControl = new FlatTreeControl<Node>(
    (node) => node.level,
    (node) => node.expandable,
  );

  dataSource = TREE.filter((node, _, list) => {
    if (node.name == 'Accueil' || node.title) {
      return true;
    }

    let permissions = [];
    if (node.expandable) {
      const childs = this.getChildNodes(list, node);

      for (let index = 0; index < childs.length; index++) {
        permissions.push(
          node.path + '_' + childs[index].path + '_' + 'consult',
        );
      }
    } else {
      const parent = this.getParentNode(list, node);

      if (parent) {
        permissions.push(parent.path + '_' + node.path + '_' + 'consult');
      } else {
        permissions.push(node.path + '_' + 'consult');
      }
    }

    return permissions.some((p) => this.auth.isUserInRole(p));
  }, this);

  currentUser!: User;

  constructor(
    private router: Router,
    private auth: KeycloakService,
    private userapi: UserApiService,
  ) {}

  ngOnInit() {
    const filter: UserFilter = {
      email: this.auth.getUsername(),
      firstname: undefined,
      lastname: undefined,
      function: undefined,
      role: undefined,
      statut: undefined,
      password: undefined,
    };
    this.userapi.getUsers(0, 1, filter).subscribe((user) => {
      if (user.length === 1) {
        this.currentUser = user[0];
      }
    });

    this.router.events.subscribe(() => {
      if (!this.router.url.startsWith('/app/file') && !this.router.url.startsWith('/app/profile')) {
        this.dataSource.forEach((n) => (n.isSelected = false));
        const node = this.findNodeForUrl(this.router.url);
        if (node) {
          node.isSelected = true;
        } else {
          console.error('No node found for url :', this.router.url);
        }
      }
    });

    this.navigateTo(this.findNodeForUrl(this.router.url));
  }

  findNodeForUrl(url: string): Node {
    const parts = url.split('/').filter((e) => e.length != 0);
    const node = this.dataSource.find((node) => node.path == parts[1])!;
    if (!node?.expandable) {
      return node;
    } else {
      return this.getChildNodes(this.dataSource, node).find(
        (node) => node.path == parts[2],
      )!;
    }
  }

  hasChild = (_: number, node: Node) => node.expandable;

  getParentNode(list: Node[], node: Node): Node | null {
    const nodeIndex = list.indexOf(node);
    for (let i = nodeIndex - 1; i >= 0; i--) {
      if (list[i].level === node.level - 1) {
        return list[i];
      }
    }
    return null;
  }

  shouldRender(node: Node): boolean {
    let parent = this.getParentNode(this.dataSource, node);
    while (parent) {
      if (!parent.isExpanded) {
        return false;
      }
      parent = this.getParentNode(this.dataSource, parent);
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
      this.navigateTo(node);
    }
  }

  getClass(node: Node): string {
    if (node.title) {
      return 'title';
    } else {
      let type = node.expandable ? 'expandable' : 'leaf';
      if (type == 'leaf' && !this.getParentNode(this.dataSource, node)) {
        type += ' no-parent';
      }
      return type + (node.isSelected ? ' selected' : '');
    }
  }

  isTitle(_: number, node: Node): boolean {
    return node.title || false;
  }

  navigateTo(node: Node): void {
    const parent = this.getParentNode(this.dataSource, node);
    if (parent) {
      this.router.navigate([
        'app',
        getRouteForNode(parent),
        getRouteForNode(node),
      ]);
    } else {
      if (!node.expandable)
        this.router.navigate(['app', getRouteForNode(node)]);
    }
  }

  openProfile() {
    this.router.navigate(['app', 'profile', this.currentUser.id]);
    this.dataSource.forEach((n) => (n.isSelected = false));
  }

  signout() {
    this.auth.logout();
  }

  getChildNodes(list: Node[], node: Node): Node[] {
    const index = list.indexOf(node);
    const level = node.level + 1;
    const result = [];
    for (let i = index + 1; i < list.length; i++) {
      if (list[i].level === level) {
        result.push(list[i]);
      } else if (list[i].level < level) {
        break;
      }
    }
    return result;
  }
}
