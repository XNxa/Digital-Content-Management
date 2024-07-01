import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { getRoutesForTabs } from './models/Tabs';
import { ZoomButtonComponent } from './shared/components/buttons/zoom-button/zoom-button.component';
import { UserListComponent } from './components/user-list/user-list.component';
import { AuthGuard } from './auth.guard';

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
                component: ZoomButtonComponent, //TODO
            },
            {
                path: 'users',
                component: UserListComponent, //TODO
                canActivate: [AuthGuard]
            },
            ...getRoutesForTabs()
        ]
    }
];
