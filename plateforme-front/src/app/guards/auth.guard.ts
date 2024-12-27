import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService); // Inject AuthService
  const router = inject(Router); // Inject Router

  // Vérifier si l'utilisateur est authentifié et si le token est valide
  if (authService.isLogged && !authService.isTokenExpired()) {
    return true; // Accès autorisé
  }

  // Redirection vers la page de login avec la redirection d'origine
  router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
  return false; // Bloquer l'accès

  return true;
};
