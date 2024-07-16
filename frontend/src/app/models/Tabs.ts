import { Route } from '@angular/router';
import { FileListComponent } from '../components/file-list/file-list.component';
import { AuthGuard } from '../auth.guard';

export interface Node {
  expandable: boolean;
  name: string;
  path: string;
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
    path: 'images',
    logo: 'image',
    level: 1,
  },
  {
    expandable: false,
    name: 'Vidéos',
    path: 'videos',
    logo: 'video',
    level: 1,
  },
  {
    expandable: false,
    name: 'Pictos',
    path: 'pictos',
    logo: 'picto',
    level: 1,
  },
  {
    expandable: false,
    name: 'Documents',
    path: 'docs',
    logo: 'doc',
    level: 1,
  },
];

export const TREE: Node[] = [
  {
    expandable: false,
    name: 'Accueil',
    path: 'home',
    level: 0,
    logo: 'home',
  },
  {
    expandable: false,
    name: 'Librairie',
    path: 'library',
    level: 0,
    logo: '',
    title: true,
  },
  {
    expandable: true,
    name: 'Web',
    path: 'web',
    level: 0,
    logo: 'desktop',
  },
  ...SUB_TREE.map((node) => ({ ...node, level: 1 })),
  {
    expandable: true,
    name: 'Mobile',
    path: 'mobile',
    level: 0,
    logo: 'mobile',
  },
  ...SUB_TREE.map((node) => ({ ...node, level: 1 })),
  {
    expandable: true,
    name: 'SM',
    path: 'sm',
    logo: 'sm',
    level: 0,
  },
  ...SUB_TREE.map((node) => ({ ...node, level: 1 })),
  {
    expandable: true,
    name: 'P.L.V',
    path: 'plv',
    logo: 'plv',
    level: 0,
  },
  ...SUB_TREE.map((node) => ({ ...node, level: 1 })),
  {
    expandable: true,
    name: 'Campagnes',
    path: 'campagnes',
    logo: 'campagnes',
    level: 0,
  },
  ...SUB_TREE.map((node) => ({ ...node, level: 1 })),
  {
    expandable: false,
    name: 'Gestion',
    path: 'management',
    logo: '',
    level: 0,
    title: true,
  },
  {
    expandable: false,
    name: 'Utilisateurs',
    path: 'users',
    logo: 'users',
    level: 0,
  },
  {
    expandable: false,
    name: 'Rôles',
    path: 'roles',
    logo: 'roles',
    level: 0,
  },
];

export function getRoutesForTabs(): Route[] {
  return TREE.filter((node) => node.expandable).map((node) => {
    return {
      path: `${getRouteForNode(node)}/:type`,
      component: FileListComponent,
      canActivate: [AuthGuard],
      data: {
        roles: [
          getRouteForNode(node) + '_images_consult',
          getRouteForNode(node) + '_videos_consult',
          getRouteForNode(node) + '_pictos_consult',
          getRouteForNode(node) + '_docs_consult',
        ],
      },
    };
  });
}

export function getRouteForNode(node: Node): string {
  return `${node.path}`;
}

export function getNameFromPath(path: string): string {
  return TREE.find((node) => node.path == path)?.name || '';
}

export function getTabForType(type: string): string {
  if (type == 'image/svg+xml') {
    return 'pictos';
  } else if (type.startsWith('image')) {
    return 'images';
  } else if (type.startsWith('video')) {
    return 'videos';
  } else {
    return 'docs';
  }
}
