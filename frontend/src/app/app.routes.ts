import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { InputComponent } from './shared/form/input/input.component';

export const routes: Routes = [
    {path: '**', component: HomeComponent}
];
