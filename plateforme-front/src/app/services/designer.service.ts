import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Designer } from '../interfaces/designer.interface';

@Injectable({
  providedIn: 'root',
})
export class DesignerService {
  private readonly apiUrl = `${environment.apiUrl}/designers`;

  constructor(private readonly http: HttpClient) {}

  private designersSubject = new BehaviorSubject<Designer[]>([]);
  designers$: Observable<Designer[]> = this.designersSubject.asObservable();

  /**
   * Récupération de tous les designers depuis l'API
   * @returns Un observable de la liste de tous les designers
   */
  loadDesigners(): Observable<Designer[]> {
    return this.http
      .get<Designer[]>(`${this.apiUrl}/all`)
      .pipe(tap((designers) => this.designersSubject.next(designers)));
  }

  /**
   * Récupération de tous les designers à partir de la liste préalablement chargée
   * @returns Un observable de la liste de tous les designers
   */
  getDesigners(): Observable<Designer[]> {
    return this.designers$;
  }

  /**
   * Création d'un nouveau designer
   */
  createDesigner(designer: Designer): Observable<Designer> {
    return this.http.post<Designer>(`${this.apiUrl}/new`, designer, {
      withCredentials: true,
    });
  }
}
