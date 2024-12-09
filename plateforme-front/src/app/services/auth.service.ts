import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { LoginRequest } from '../interfaces/login-request.interface';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;

  private readonly TOKEN_NAME = 'jwt';

  public isLogged = false;
  private isLoggedSubject = new BehaviorSubject<boolean>(this.isLogged);

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {
    this.checkTokenPresence();
  }

  /**
   * Envoie une requête de connexion et retourne l'observable du token (string).
   */
  login(email: string, password: string): Observable<string> {
    const loginRequest: LoginRequest = { email, password };
    return this.http.post<string>(`${this.apiUrl}/login`, loginRequest, {
      withCredentials: true,
      responseType: 'text' as 'json',
    });
  }

  /**
   * Retourne un observable pour suivre l'état de connexion de l'utilisateur.
   */
  $isLogged(): Observable<boolean> {
    return this.isLoggedSubject.asObservable();
  }

  /**
   * Vérifie la présence d'un token et met à jour l'état de connexion.
   */
  checkTokenPresence(): void {
    const token = this.getTokenFromCookie();
    this.isLogged = !!token;
    console.log("islogged dans auth service : " + this.isLogged);
    this.next();
  }

  /**
   * Vérifie si le token est expiré.
   */
  isTokenExpired(): boolean {
    const token = this.getTokenFromCookie();
    if (!token) return true;

    try {
      const expiry = JSON.parse(atob(token.split('.')[1])).exp;
      return Math.floor(Date.now() / 1000) >= expiry;
    } catch {
      return true; // Considérer expiré en cas d'erreur
    }
  }

  /**
   * Déconnexion : supprime le token et notifie le backend.
   */
  logout(): void {
    this.http
      .post(`${this.apiUrl}/logout`, {}, { withCredentials: true })
      .subscribe({
        next: () => {
          this.clearToken();
          this.router.navigate(['/login']);
        },
        error: () => {
          console.error('Erreur lors de la déconnexion.');
          this.clearToken();
          this.router.navigate(['/login']);
        },
      });
  }

  /**
   * Supprime le cookie contenant le token.
   */
  private clearToken(): void {
    document.cookie = `${this.TOKEN_NAME}=; path=/; max-age=0;`;
    this.isLogged = false;
    this.next();
  }

  /**
   * Notifie les abonnés de l'état de connexion.
   */
  private next(): void {
    this.isLoggedSubject.next(this.isLogged);
  }

  /**
   * Récupère le token depuis les cookies.
   */
  private getTokenFromCookie(): string | null {
    const name = `${this.TOKEN_NAME}=`;
    const decodedCookie = decodeURIComponent(document.cookie);
    const cookiesArray = decodedCookie.split(';');

    for (let cookie of cookiesArray) {
      cookie = cookie.trim();
      if (cookie.startsWith(name)) {
        return cookie.substring(name.length);
      }
    }
    return null;
  }
}
