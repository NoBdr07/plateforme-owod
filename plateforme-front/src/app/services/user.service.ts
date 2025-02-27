import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { User } from '../interfaces/user.interface';
import { Designer } from '../interfaces/designer.interface';

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

  getUserFriends(): Observable<Designer[]> {
    return this.http.get<Designer[]>(`${this.apiUrl}/friends`, {
      withCredentials: true
    })
  }

  addFriend(friendId: string): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/add/${friendId}`, {}, {
      withCredentials: true
    })
  }
  
  deleteFriend(friendId: string): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/delete/${friendId}`, {}, {
      withCredentials: true
    })
  }
  
}
