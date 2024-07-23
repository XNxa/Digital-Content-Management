import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { getRoutesForTabs } from './models/Tabs';
import { UserListComponent } from './components/user-list/user-list.component';
import { AuthGuard } from './auth.guard';
import { RoleListComponent } from './components/role-list/role-list.component';
import { NewRoleComponent } from './components/new-role/new-role.component';
import { ModifyRoleComponent } from './components/modify-role/modify-role.component';
import { ModifyUserComponent } from './components/modify-user/modify-user.component';
import { HomePageComponent } from './components/home-page/home-page.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full',
      },
      {
        path: 'home',
        component: HomePageComponent, //TODO
      },
      {
        path: 'roles',
        component: RoleListComponent,
        canActivate: [AuthGuard],
        data: { roles: ['role_consult'] },
      },
      {
        path: 'role/:id',
        component: ModifyRoleComponent,
        canActivate: [AuthGuard],
        data: { roles: ['role_consult'] },
      },
      {
        path: 'roles/add',
        component: NewRoleComponent,
        canActivate: [AuthGuard],
        data: { roles: ['role_consult'] },
      },
      {
        path: 'users',
        component: UserListComponent,
        canActivate: [AuthGuard],
        data: { roles: ['user_consult'] },
      },
      {
        path: 'user/:id',
        component: ModifyUserComponent,
        canActivate: [AuthGuard],
        data: { roles: ['user_consult'] },
      },
      {
        path: 'profile/:id',
        component: UserProfileComponent,
        canActivate: [AuthGuard],
      },
      ...getRoutesForTabs(),
    ],
  },
];
