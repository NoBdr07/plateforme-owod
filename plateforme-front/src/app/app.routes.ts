import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';

export const routes: Routes = [
    {
        path: '',
        component: HomeComponent
    },
    {
        path: 'login',
        loadComponent: () => 
            import(
                './login/login.component'
            ).then((c) => c.LoginComponent)
    },
    {
        path: 'register',
        loadComponent: () =>
            import(
                './register/register.component'
            ).then((c) => c.RegisterComponent)
    },
    {
        path: 'details',
        loadComponent: () => 
            import(
                './designer-details/designer-details.component'
            ).then((c) => c.DesignerDetailsComponent)
    },
    {
        path: 'account',
        loadComponent: () =>
            import(
              './my-account/my-account.component'
            ).then((c) => c.MyAccountComponent),
    }
];
