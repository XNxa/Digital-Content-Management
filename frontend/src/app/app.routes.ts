import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';

export const routes: Routes = [
    {path: '**', component: SidebarComponent}
];
