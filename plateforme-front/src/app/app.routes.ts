import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { authGuard } from './shared/guards/auth.guard';

export const routes: Routes = [
    {
        path: '',
        component: HomeComponent
    },
    {
        path: 'login',
        loadComponent: () => 
            import(
                './auth/login/login.component'
            ).then((c) => c.LoginComponent)
    },
    {
        path: 'register',
        loadComponent: () =>
            import(
                './auth/register/register.component'
            ).then((c) => c.RegisterComponent)
    },
    {
        path: 'details/:id',
        loadComponent: () => 
            import(
                './designers/designer-details/designer-details.component'
            ).then((c) => c.DesignerDetailsComponent)
    },
    {
        path: 'account',
        loadComponent: () =>
            import(
              './personnal-space/my-account/my-account.component'
            ).then((c) => c.MyAccountComponent),
        canActivate: [authGuard]
    },
    {
        path: 'account/contacts',
        loadComponent: () =>
            import(
              './personnal-space/contacts/contacts.component'
            ).then((c) => c.ContactsComponent),
        canActivate: [authGuard]
    },
    {
        path: 'account/gestion-calendrier',
        loadComponent: () =>
            import(
              './personnal-space/gestion-calendrier/gestion-calendrier.component'
            ).then((c) => c.GestionCalendrierComponent),
        canActivate: [authGuard]
    },
    {
        path: 'account/add-designers',
        loadComponent: () =>
            import(
              './personnal-space/add-designers/add-designers.component'
            ).then((c) => c.AddDesignersComponent),
        canActivate: [authGuard]
    },
    {
        path: 'catalogue',
        loadComponent: () =>
            import(
              './designers/catalogue/catalogue.component'
            ).then((c) => c.CatalogueComponent),
    },
    {
        path: 'dashboard',
        loadComponent: () =>
            import(
              './personnal-space/dashboard/dashboard.component'
            ).then((c) => c.DashboardComponent),
        canActivate: [authGuard]
    },
    {
        path: 'reset-password',
        loadComponent: () => 
            import(
                './auth/reset-password/reset-password.component'
            ).then((c) => c.ResetPasswordComponent),
    },
    {
        path: 'about',
        loadComponent: () => 
            import(
                './pages/about/about.component'
            ).then((c) => c.AboutComponent),
    },
    {
        path: 'faq',
        loadComponent: () => 
            import(
                './pages/faq/faq.component'
            ).then((c) => c.FaqComponent),
    },
    {
        path: 'mentions-legales',
        loadComponent: () => 
            import(
                './pages/mentions-legales/mentions-legales.component'
            ).then((c) => c.MentionsLegalesComponent),
    },
    {
        path: 'contact',
        loadComponent: () => 
            import(
                './pages/contact/contact.component'
            ).then((c) => c.ContactComponent),
    },
    {
        path: '**',
        redirectTo: '',
        pathMatch: 'full'
    }
];
