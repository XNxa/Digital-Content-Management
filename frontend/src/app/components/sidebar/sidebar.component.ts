import { Component } from '@angular/core';
import { CdkTreeModule, FlatTreeControl } from '@angular/cdk/tree';
import { UserCardFooterComponent } from '../../shared/components/user-card-footer/user-card-footer.component';

interface Node {
  expandable: boolean;
  name: string;
  isExpanded?: boolean;
  logo: string;
  level: number;
  isSelected?: boolean;
  title?: boolean;
}

const SUB_TREE: Node[] = [
  {
    expandable: false,
    name: 'Images',
    logo: 'image',
    level: 1,
  },
  {
    expandable: false,
    name: 'Vidéos',
    logo: 'video',
    level: 1,
  },
  {
    expandable: false,
    name: 'Pictos',
    logo: 'picto',
    level: 1,
  },
  {
    expandable: false,
    name: 'Documents',
    logo: 'doc',
    level: 1,
  },
];

const TREE: Node[] = [
  {
    expandable: false,
    name: 'Accueil',
    level: 0,
    logo: 'home',
  },
  {
    expandable: false,
    name: 'Librairie',
    level: 0,
    logo: '',
    title: true
  },
  {
    expandable: true,
    name: 'Web',
    level: 0,
    logo: 'desktop',
  },
  ...SUB_TREE.map(node => ({ ...node, level: 1 })),
  {
    expandable: true,
    name: 'Mobile',
    logo: 'mobile',
    level: 0,
  },
  ...SUB_TREE.map(node => ({ ...node, level: 1 })),
  {
    expandable: true,
    name: 'SM',
    logo: 'sm',
    level: 0,
  },
  ...SUB_TREE.map(node => ({ ...node, level: 1 })),
  {
    expandable: true,
    name: 'P.L.V',
    logo: 'plv',
    level: 0,
  },
  ...SUB_TREE.map(node => ({ ...node, level: 1 })),
  {
    expandable: true,
    name: 'Campagnes',
    logo: 'campagnes',
    level: 0,
  },
  ...SUB_TREE.map(node => ({ ...node, level: 1 })),
  {
    expandable: false,
    name: 'Gestion',
    logo: '',
    level: 0,
    title: true
  },
  {
    expandable: false,
    name: 'Utilisateur',
    logo: 'users',
    level: 0,
  },
  {
    expandable: false,
    name: 'Rôles',
    logo: 'roles',
    level: 0,
  },
];

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
    return `icons/sidebar/${node.isSelected ? 'blue' : 'gray'}/arrow-${node.isExpanded ? 'open': 'close'}.svg`;
  }

  selectNode(node: Node): void {
    if (node.expandable) {
      node.isExpanded = !node.isExpanded;
    } else {
      // Deselect all nodes
      this.dataSource.forEach(n => n.isSelected = false);
  
  
      // Select the clicked node
      node.isSelected = true;
  
      if (node.expandable) {
        this.dataSource.forEach(n => { n.isExpanded = false; });
        node.isExpanded = true;
      }
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

  isTitle(_:number, node: Node): boolean {
    return node.title || false;
  }
}
