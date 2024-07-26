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
import { FileDetailsComponent } from './components/file-details/file-details.component';

export const routes: Routes = [
  {
    path:'',
    pathMatch: 'full',
    redirectTo: 'app',
  },
  {
    path: 'app',
    component: HomeComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: '/app/home',
      },
      {
        path: 'home',
        component: HomePageComponent, //TODO
      },
      {
        path: 'role',
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
        path: 'role/add',
        component: NewRoleComponent,
        canActivate: [AuthGuard],
        data: { roles: ['role_consult'] },
      },
      {
        path: 'user',
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
  {
    path: 'app/file/:id',
    component: FileDetailsComponent,
    canActivate: [AuthGuard],
  },
];
