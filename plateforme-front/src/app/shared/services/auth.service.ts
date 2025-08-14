import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { BehaviorSubject, catchError, map, Observable, of, startWith, switchMap, tap, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { LoginRequest } from '../interfaces/login-request.interface';
import { RegisterRequest } from '../interfaces/register-request.interface';
import { UserInfo } from '../interfaces/user-info.interface';
import { SessionState } from '../interfaces/session-state.interface';
import { AccountType } from '../enums/account-type.enum';
import { User } from '../interfaces/user.interface';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  
  private _session$ = new BehaviorSubject<SessionState> ({
    isLogged: false,
    isAdmin : false,
    userId: null,
    accountType: AccountType.NONE,
    user: null,
    designerId: null,
    companyId: null,
  })

  public readonly session$ = this._session$.asObservable();

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {
    this.refreshSession().subscribe();
  }

  /**
   * Met à jour l'état de connexion.
   */
  refreshSession(): Observable<SessionState> {
    return this.http.get<UserInfo>(`${this.apiUrl}/auth/me`).pipe(
      switchMap((me) => {
        const base: SessionState = {
          isLogged: true,
          isAdmin: me.roles.includes('ROLE_ADMIN'),
          userId: me.userId,
          accountType: AccountType.NONE,
          user: null,
          designerId: null,
          companyId: null,
        };

        return this.http.get<AccountType>(
          `${this.apiUrl}/users/${me.userId}/has-account`).pipe(
            switchMap(res => {
              const state = {...base, accountType: res };
              
              return this.http.get<User>(`${this.apiUrl}/users/${me.userId}`).pipe(
                map(user => ({...state, user})),
                catchError(() => of(state))
              );
            })
          );
      }),
      tap((s) => this._session$.next(s)),
      catchError(err => {
        this._session$.next({
          isLogged: false,
          isAdmin: false,
          userId: null,
          accountType: AccountType.NONE,
          user: null,
          designerId: null,
          companyId: null,
        });
        return throwError(() => err);
      })
    )
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
        switchMap(() => this.refreshSession()),
        map(() => 'OK')
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
   * Déconnexion : envoie la requête de deconnexion au back end puis appelle handleLogout().
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
      .subscribe(() => {
        this._session$.next({
          isLogged: false, isAdmin: false, userId: null, accountType: AccountType.NONE, user: null,
          designerId: null, companyId: null,
        });
        this.router.navigate(['/login']);
      }
      );
  }

  /** Accès ponctuel */
  getUserId(): string | null { return this._session$.value.userId; }
  get isAdmin(): boolean { return this._session$.value.isAdmin; }

}
