import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { getRoutesForTabs } from './models/Tabs';
import { AuthGuard } from './auth.guard';
import { HomePageComponent } from './components/home-page/home-page.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';

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
        component: HomePageComponent,
      },
      {
        path: 'role',
        //component: RoleListComponent,
        loadComponent: () => import('./components/role-list/role-list.component').then(m => m.RoleListComponent),
        canActivate: [AuthGuard],
        data: { roles: ['role_consult'] },
      },
      {
        path: 'role/:id',
        //component: ModifyRoleComponent,
        loadComponent: () => import('./components/modify-role/modify-role.component').then(m => m.ModifyRoleComponent),
        canActivate: [AuthGuard],
        data: { roles: ['role_consult'] },
      },
      {
        path: 'roles/add',
        //component: NewRoleComponent,
        loadComponent: () => import('./components/new-role/new-role.component').then(m => m.NewRoleComponent),
        canActivate: [AuthGuard],
        data: { roles: ['role_consult'] },
      },
      {
        path: 'user',
        //component: UserListComponent,
        loadComponent: () => import('./components/user-list/user-list.component').then(m => m.UserListComponent),
        canActivate: [AuthGuard],
        data: { roles: ['user_consult'] },
      },
      {
        path: 'user/:id',
        //component: ModifyUserComponent,
        loadComponent: () => import('./components/modify-user/modify-user.component').then(m => m.ModifyUserComponent),
        canActivate: [AuthGuard],
        data: { roles: ['user_consult'] },
      },
      {
        path: 'profile/:id',
        //loadComponent: () => import('./components/user-profile/user-profile.component').then(m => m.UserProfileComponent),
        component: UserProfileComponent,
        canActivate: [AuthGuard],
      },
      ...getRoutesForTabs(),
    ],
  },
  {
    path: 'app/file/:id',
    //component: FileDetailsComponent,
    loadComponent: () => import('./components/file-details/file-details.component').then(m => m.FileDetailsComponent),
    canActivate: [AuthGuard],
  },
];
