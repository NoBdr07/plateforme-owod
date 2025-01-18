import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { LoginRequest } from '../interfaces/login-request.interface';
import { RegisterRequest } from '../interfaces/register-request.interface';

interface UserInfo {
  userId: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private userId: string | null = null;

  public isLogged = false;
  private isLoggedSubject = new BehaviorSubject<boolean>(this.isLogged);
  private lastTokenCheck: number = 0;
  private readonly TOKEN_CHECK_INTERVAL = 30000; // 30 secondes

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {
    this.checkAuthStatus();
  }

  /**
   * Envoie une requête de connexion et retourne l'observable du token (string).
   */
  login(email: string, password: string): Observable<string> {
    const loginRequest: LoginRequest = { email, password };
    return this.http
      .post<string>(`${this.apiUrl}/login`, loginRequest, {
        withCredentials: true,
        responseType: 'text' as 'json',
      })
      .pipe(
        tap(() => {
          this.isLogged = true;
          this.checkAuthStatus();
          this.next();
        }),
        catchError((error) => {
          this.isLogged = false;
          this.userId = null;
          this.next();
          return throwError(() => error);
        })
      );
  }

  /**
   * Envoie un requête d'inscription
   */
  register(registerRequest: RegisterRequest): Observable<any> {
    return this.http
      .post(`${this.apiUrl}/register`, registerRequest, {
        withCredentials: true,
        responseType: 'text' as 'json',
      })
      .pipe(
        catchError((error) => {
          if (error.status === 400) {
            // Renvoie le message d'erreur du backend
            return throwError(() => ({ message: error.error }));
          }
          return throwError(() => ({
            message: "Une erreur est survenue lors de l'inscription",
          }));
        })
      );
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
  checkAuthStatus(): void {
    const now = Date.now();
    if (now - this.lastTokenCheck < this.TOKEN_CHECK_INTERVAL) {
      return;
    }

    this.lastTokenCheck = now;

    this.http
      .get<UserInfo>(`${this.apiUrl}/me`, {
        withCredentials: true,
      })
      .subscribe({
        next: (userInfo) => {
          this.isLogged = true;
          this.userId = userInfo.userId;
          this.next();
        },
        error: () => {
          this.isLogged = false;
          this.userId = null;
          this.next();
        },
      });
  }

  /**
   * Déconnexion : supprime le token et notifie le backend.
   */
  logout(): void {
    this.http
      .post(
        `${this.apiUrl}/logout`,
        {},
        {
          withCredentials: true,
          responseType: 'text' as 'json',
        }
      )
      .subscribe({
        next: () => {
          this.handleLogout();
        },
      });
  }

  private handleLogout(): void {
    this.isLogged = false;
    this.userId = null;
    this.next();
    this.router.navigate(['/login']);
  }

  getUserId(): string | null {
    return this.userId;
  }


  /**
   * Notifie les abonnés de l'état de connexion.
   */
  private next(): void {
    this.isLoggedSubject.next(this.isLogged);
  }
}
