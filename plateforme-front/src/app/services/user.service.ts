import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { User } from '../interfaces/user.interface';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(
    private readonly http: HttpClient,
  ) {}

  /**
   * Envoie une requete pour tester si l'utilisateur a déjà un compte designer
   */
  hasAnAccount(userId: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/${userId}/has-designer`, {
      withCredentials: true
    });
  }

  /**
   * Récupère le nom et prénom d'un utilisateur
   * @param userId 
   * @returns 
   */
  getUser(userId: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${userId}`, {
      withCredentials: true
    });
  }
  
  
}
