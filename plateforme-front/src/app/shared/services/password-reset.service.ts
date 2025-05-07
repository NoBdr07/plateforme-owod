import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PasswordResetService {
  private apiUrl = `${environment.apiUrl}/password`;

  constructor(private readonly http: HttpClient) {}

  /**
   * Envoie la requete au back qui verifie l'existence de l'utilisateur et envoie le mail de reinitialisation du mot de passe 
   * @param email 
   * @returns 
   */
  requestReset(email: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/request-reset`, email);
  }

  /**
   * Envoie la requête de réinitialisation du mot de passe
   * @param token (envoyé dans l'email de réinitialisation et présent dans l'url)
   * @param newPassword 
   * @returns 
   */
  resetPassword(token: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/reset`, { token, newPassword });
  }
}
