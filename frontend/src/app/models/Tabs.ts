import { Route } from "@angular/router";
import { FileListComponent } from "../components/file-list/file-list.component";

export interface Node {
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

export const TREE: Node[] = [
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
        name: 'Utilisateurs',
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

export function getRoutesForTabs(): Route[] {
    return TREE.filter(node => node.expandable).map(node => {
        return {
            path: `${getRouteForNode(node)}/:type`,
            component: FileListComponent
        };
    });
}

export function getRouteForNode(node: Node): string {
    return `${node.name.toLowerCase().replaceAll('.', '',).replace('ô', 'o')}`;
}