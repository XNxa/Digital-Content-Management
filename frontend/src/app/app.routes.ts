import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { getRoutesForTabs } from './models/Tabs';
import { ZoomButtonComponent } from './shared/components/buttons/zoom-button/zoom-button.component';
import { UserListComponent } from './components/user-list/user-list.component';
import { AuthGuard } from './auth.guard';
import { AddUserDialogComponent } from './components/add-user-dialog/add-user-dialog.component';
import { RoleListComponent } from './components/role-list/role-list.component';
import { NewRoleComponent } from './components/new-role/new-role.component';

export const routes: Routes = [
    {
        path: '',
        component: HomeComponent,
        children: [
            {
                path: '',
                redirectTo: 'accueil',
                pathMatch: 'full'
            },
            {
                path: 'home',
                component: ZoomButtonComponent, //TODO
            },
            {
                path: 'roles',
                component: RoleListComponent, 
                canActivate: [AuthGuard]
            },
            {
                path: 'roles/add',
                component: NewRoleComponent,
                canActivate: [AuthGuard]
            },
            {
                path: 'users',
                component: UserListComponent,
                canActivate: [AuthGuard]
            },
            ...getRoutesForTabs()
        ]
    }
];
