import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { getRoutesForTabs } from './models/Tabs';
import { ZoomButtonComponent } from './shared/components/buttons/zoom-button/zoom-button.component';

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
                path: 'accueil',
                component: ZoomButtonComponent, //TODO
            },
            {
                path: 'roles',
                component: ZoomButtonComponent, //TODO
            },
            {
                path: 'utilisateurs',
                component: ZoomButtonComponent, //TODO
            },
            ...getRoutesForTabs()
        ]
    }
];
