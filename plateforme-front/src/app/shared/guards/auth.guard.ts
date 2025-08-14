import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { catchError, map, of, take } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService); 
  const router = inject(Router); 

  /**
   * Interdit la route si l'utilisateur n'est pas connecté
   */
  return authService.session$.pipe(
    take(1),
    map(s => {
      if (s.isLogged) return true;
      router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
      return false;
    })
  );
};
