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
   * @returns l'observable du User
   */
  getUser(userId: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${userId}`, {
      withCredentials: true
    });
  }

  /**
   * Récupération des contacts enregistré de l'utilisateur
   * @returns un observable de la liste des designers
   */
  getUserFriends(): Observable<Designer[]> {
    return this.http.get<Designer[]>(`${this.apiUrl}/friends`, {
      withCredentials: true
    })
  }

  /**
   * Ajout d'un contact
   * @param friendId 
   * @returns l'observable de l'utilisateur avec ces amis mis à jour
   */
  addFriend(friendId: string): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/add/${friendId}`, {}, {
      withCredentials: true
    })
  }
  
  /**
   * Suppression d'un contact enregistré
   * @param friendId 
   * @returns l'observable de l'utilisateur avec ces contacts mis à jour
   */
  deleteFriend(friendId: string): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/delete/${friendId}`, {}, {
      withCredentials: true
    })
  }
  
}
