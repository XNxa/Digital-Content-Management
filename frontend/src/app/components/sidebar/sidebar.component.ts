import { Component } from '@angular/core';
import { CdkTreeModule, FlatTreeControl } from '@angular/cdk/tree';
import { UserCardFooterComponent } from '../../shared/components/user-card-footer/user-card-footer.component';
import { TREE, Node, getRouteForNode } from '../../models/Tabs';
import { Router } from '@angular/router';


@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CdkTreeModule, UserCardFooterComponent],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  treeControl = new FlatTreeControl<Node>(
    node => node.level, node => node.expandable
  );

  dataSource = TREE.map(node => {
    node.isSelected = node.name == 'Accueil';
    return node;
  });

  constructor(private router: Router) { }

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
      this.dataSource.forEach(n => { if (n.name != node.name) n.isExpanded = false; });
      node.isExpanded = !node.isExpanded;
    } else {
      this.dataSource.forEach(n => n.isSelected = false);
      node.isSelected = true;
      this.navigateTo(node);
    }
  }

  getClass(node: Node): string {
    if (node.title) {
      return 'title';
    } else {
      const type = node.expandable ? 'expandable' : 'leaf';
      return type + (node.isSelected ? ' selected' : '');
    }
  }

  isTitle(_: number, node: Node): boolean {
    return node.title || false;
  }

  navigateTo(node: Node): void {
    let parent = this.getParentNode(node);
    if (parent) {
      this.router.navigate([getRouteForNode(parent), node.name.toLowerCase()]);
    } else {
      this.router.navigate([getRouteForNode(node)]);
    }
  }


}
