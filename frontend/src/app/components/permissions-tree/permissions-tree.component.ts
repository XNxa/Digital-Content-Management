import { CdkTreeModule, FlatTreeControl } from '@angular/cdk/tree';
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { RoleApiService } from '../../services/role-api.service';
import { Permission } from '../../models/Permission';
import { ToggleButtonComponent } from '../../shared/components/buttons/toggle-button/toggle-button.component';

export interface PermissionNode {
  level: number;
  name: string;
  expandable: boolean;
  expanded?: boolean;
  permission?: string;
  children?: PermissionNode[];
  hidden?: boolean;
  toggle: boolean;
}

@Component({
  selector: 'app-permissions-tree',
  standalone: true,
  imports: [CdkTreeModule, ToggleButtonComponent],
  templateUrl: './permissions-tree.component.html',
  styleUrl: './permissions-tree.component.css'
})
export class PermissionsTreeComponent implements OnInit {

  @Output() selectedPermissions: EventEmitter<Set<string>> = new EventEmitter<Set<string>>();

  treeControl = new FlatTreeControl<PermissionNode>(
    node => node.level, node => node.expandable
  );

  dataSource!: PermissionNode[];

  isFolder = (_: number, node: PermissionNode) => node.level == 0;
  isSubfolder = (_: number, node: PermissionNode) => node.level == 1 && !node.hidden;
  isHiddenSubfolder = (_: number, node: PermissionNode) => node.level == 1 && node.hidden!;
  isLeaf = (_: number, node: PermissionNode) => node.level == 2;

  constructor(private api: RoleApiService) { }

  ngOnInit(): void {
    this.api.getPermissions().subscribe(permissions => {
      this.buildDataSource(permissions);
    });
  }

  buildDataSource(permissions: Permission[]): void {
    const tree: PermissionNode[] = [];
    const map = new Map<string, PermissionNode>();

    permissions.forEach(permission => {
      if (!map.has(permission.folder)) {
        const folderNode: PermissionNode = { name: permission.folder, expandable: true, toggle: false, level: 0 };
        map.set(permission.folder, folderNode);
        tree.push(folderNode);
      }
      const folderNode = map.get(permission.folder)!;

      if (permission.subfolder) {
        const subfolderKey = `${permission.folder}/${permission.subfolder}`;
        if (!map.has(subfolderKey)) {
          const subfolderNode: PermissionNode = { name: permission.subfolder, expandable: true, toggle: false, level: 1 };
          map.set(subfolderKey, subfolderNode);
          folderNode.children = folderNode.children || [];
          folderNode.children.push(subfolderNode);
        }
        const subfolderNode = map.get(subfolderKey)!;
        subfolderNode.children = subfolderNode.children || [];
        subfolderNode.children.push({ name: permission.name, permission: permission.permission, expandable: false, toggle: false, level: 2 });
      } else {
        const noSubfolderKey = `${permission.folder}/no-subfolder`;
        if (!map.has(noSubfolderKey)) {
          const noSubfolderNode: PermissionNode = { name: '', expandable: false, expanded: true, level: 1, toggle:false, hidden: true };
          map.set(noSubfolderKey, noSubfolderNode);
          folderNode.children = folderNode.children || [];
          folderNode.children.push(noSubfolderNode);
        }
        const noSubfolderNode = map.get(noSubfolderKey)!;
        noSubfolderNode.children = noSubfolderNode.children || [];
        noSubfolderNode.children.push({ name: permission.name, permission: permission.permission, expandable: false, toggle: false, level: 2 });
      }
    });

    this.dataSource = this.flatten(tree);
  }

  flatten(tree: PermissionNode[]): PermissionNode[] {
    const result: PermissionNode[] = [];
    tree.forEach(node => {
      result.push(node);
      if (node.children) {
        result.push(...this.flatten(node.children));
      }
    });
    result.forEach(node => { if (node.expandable) node.expanded = false; });
    return result;
  }

  getParentNode(node: PermissionNode): PermissionNode | null {
    const nodeIndex = this.dataSource.indexOf(node);
    for (let i = nodeIndex - 1; i >= 0; i--) {
      if (this.dataSource[i].level == node.level - 1) {
        return this.dataSource[i];
      }
    }
    return null;
  }

  shouldRender(node: PermissionNode): boolean {
    let parent = this.getParentNode(node);
    while (parent) {
      if (!parent.expanded) {
        return false;
      }
      parent = this.getParentNode(parent);
    }
    return true;
  }

  nodeClicked(node: PermissionNode): void {
    node.expanded = !node.expanded;
  }

  getFolderClass(node: PermissionNode): string {
    return 'folder' + (!node.expanded && node.name == 'Rôles' ? ' last' : '');
  }

  setAll(value: boolean): void {
    this.dataSource.forEach(node => {
      node.toggle = value;
    });

    this.selectedPermissions.emit(new Set(this.dataSource.filter(node => node.toggle).map(node => node.permission!)));
  }

  toggle(node: PermissionNode): void {
    if (node.level == 0) {
      if (!node.toggle) {
        node.toggle = true;
        node.children!.forEach(child => {
          child.toggle = true; 
          child.children!.forEach(grandchild => grandchild.toggle = true);
        });
      } else {
        node.toggle = false;
      }
    }
    else if (node.level == 1) {
      if (!node.toggle) {
        node.toggle = true;
        node.children!.forEach(child => child.toggle = true);
        let parent = this.getParentNode(node);
        if (parent!.children!.every(child => child.toggle)) {
          parent!.toggle = true;
        }
      } else {
        node.toggle = false;
        let parent = this.getParentNode(node);
        parent!.toggle = false;
      }
    }
    else {
      if (node.toggle) {
        node.toggle = false;
        let parent = this.getParentNode(node);
        while (parent) {
          parent.toggle = false;
          parent = this.getParentNode(parent);
        }
      } else {
        node.toggle = true;
        let parent = this.getParentNode(node);
        while (parent) {
          if (parent.children!.every(child => child.toggle)) {
            parent.toggle = true;
          }
          parent = this.getParentNode(parent);
        }
      }
    }

    this.selectedPermissions.emit(new Set(this.dataSource.filter(node => node.toggle).map(node => node.permission!)));
  }
}

