import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { authGuard } from './guards/auth.guard';

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
        path: 'details/:id',
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
        canActivate: [authGuard]
    },
    {
        path: 'account/contacts',
        loadComponent: () =>
            import(
              './contacts/contacts.component'
            ).then((c) => c.ContactsComponent),
        canActivate: [authGuard]
    },
    {
        path: 'catalogue',
        loadComponent: () =>
            import(
              './catalogue/catalogue.component'
            ).then((c) => c.CatalogueComponent),
    },
    {
        path: 'dashboard',
        loadComponent: () =>
            import(
              './dashboard/dashboard.component'
            ).then((c) => c.DashboardComponent),
        canActivate: [authGuard]
    },
    {
        path: 'reset-password',
        loadComponent: () => 
            import(
                './reset-password/reset-password.component'
            ).then((c) => c.ResetPasswordComponent),
    },
    {
        path: 'about',
        loadComponent: () => 
            import(
                './about/about.component'
            ).then((c) => c.AboutComponent),
    },
    {
        path: 'faq',
        loadComponent: () => 
            import(
                './faq/faq.component'
            ).then((c) => c.FaqComponent),
    },
    {
        path: 'mentions-legales',
        loadComponent: () => 
            import(
                './mentions-legales/mentions-legales.component'
            ).then((c) => c.MentionsLegalesComponent),
    },
    {
        path: 'contact',
        loadComponent: () => 
            import(
                './contact/contact.component'
            ).then((c) => c.ContactComponent),
    },
    {
        path: '**',
        redirectTo: '',
        pathMatch: 'full'
    }
];
