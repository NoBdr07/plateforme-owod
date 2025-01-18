import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService); // Inject AuthService
  const router = inject(Router); // Inject Router

  authService.checkAuthStatus();

  if(authService.isLogged) {
    return true;
  }

  router.navigate(['/login'], { queryParams: { returnUrl: state.url }});

  return false;
};
