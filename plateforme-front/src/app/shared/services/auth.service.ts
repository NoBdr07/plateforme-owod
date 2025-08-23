import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { BehaviorSubject, catchError, forkJoin, map, Observable, of, startWith, switchMap, tap, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { LoginRequest } from '../interfaces/login-request.interface';
import { RegisterRequest } from '../interfaces/register-request.interface';
import { SessionInfo } from '../interfaces/session-info.interface';
import { SessionState } from '../interfaces/session-state.interface';
import { AccountType } from '../enums/account-type.enum';
import { User } from '../interfaces/user.interface';
import { UserService } from './user.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;

  private _session$ = new BehaviorSubject<SessionState>({
    isLogged: false,
    isAdmin: false,
    userId: null,
    firstname: null,
    lastname: null,
    accountType: AccountType.NONE,
    designerId: null,
    companyId: null,
  })

  public readonly session$ = this._session$.asObservable();

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router,
    private readonly userService: UserService
  ) {
    this.refreshSession().subscribe();
  }

  /**
   * Met à jour l'état de connexion.
   */
  refreshSession(): Observable<SessionState> {
    return this.http.get<SessionInfo>(`${this.apiUrl}/me`, { withCredentials: true }).pipe(
      map((me) => {
        const state: SessionState = {
          isLogged: true,
          isAdmin: me.roles.includes('ROLE_ADMIN'),
          userId: me.userId,
          firstname: me.firstname,
          lastname: me.lastname,
          accountType: me.accountType as AccountType,
          designerId: me.designerId,
          companyId: me.companyId,
        };
        return state;
      }),
      tap((s) => this._session$.next(s)),
      catchError(err => {
        // remet l'état à "déconnecté"
        this._session$.next({
          isLogged: false,
          isAdmin: false,
          userId: null,
          firstname: null,
          lastname: null,
          accountType: AccountType.NONE,
          designerId: null,
          companyId: null,
        });
        return throwError(() => err);
      })
    );
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
          isLogged: false, 
          isAdmin: false, 
          userId: null, 
          firstname: null,
          lastname: null,
          accountType: AccountType.NONE,
          designerId: null, 
          companyId: null,
        });
        this.router.navigate(['/login']);
      }
      );
  }

  /** Accès ponctuel */
  getUserId(): string | null { return this._session$.value.userId; }
  get isAdmin(): boolean { return this._session$.value.isAdmin; }

}
