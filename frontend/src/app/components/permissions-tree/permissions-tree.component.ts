import { CdkTreeModule, FlatTreeControl } from '@angular/cdk/tree';
import { Component, OnInit } from '@angular/core';
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
}

@Component({
  selector: 'app-permissions-tree',
  standalone: true,
  imports: [CdkTreeModule, ToggleButtonComponent],
  templateUrl: './permissions-tree.component.html',
  styleUrl: './permissions-tree.component.css'
})
export class PermissionsTreeComponent implements OnInit {
  treeControl = new FlatTreeControl<PermissionNode>(
    node => node.level, node => node.expandable
  );

  dataSource!: PermissionNode[];

  isFolder = (_: number, node: PermissionNode) => node.level == 0;
  isSubfolder = (_: number, node: PermissionNode) => node.level == 1;
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
        const folderNode: PermissionNode = { name: permission.folder, expandable: true, level: 0 };
        map.set(permission.folder, folderNode);
        tree.push(folderNode);
      }
      const folderNode = map.get(permission.folder)!;

      if (permission.subfolder) {
        const subfolderKey = `${permission.folder}/${permission.subfolder}`;
        if (!map.has(subfolderKey)) {
          const subfolderNode: PermissionNode = { name: permission.subfolder, expandable: true, level: 1 };
          map.set(subfolderKey, subfolderNode);
          folderNode.children = folderNode.children || [];
          folderNode.children.push(subfolderNode);
        }
        const subfolderNode = map.get(subfolderKey)!;
        subfolderNode.children = subfolderNode.children || [];
        subfolderNode.children.push({ name: permission.name, permission: permission.permission, expandable: false, level: 2 });
      } else {
        folderNode.children = folderNode.children || [];
        folderNode.children.push({ name: permission.name, permission: permission.permission, expandable: false, level: 1 });
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

}
