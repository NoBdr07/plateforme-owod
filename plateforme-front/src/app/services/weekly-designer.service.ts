import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { Designer } from '../interfaces/designer.interface';

@Injectable({
  providedIn: 'root',
})
export class WeeklyDesignerService {
  private readonly apiUrl = `${environment.apiUrl}/weekly`;

  private weeklyDesignerSubject = new BehaviorSubject<Designer | null>(null);
  weeklyDesigner$ = this.weeklyDesignerSubject.asObservable();

  constructor(private readonly http: HttpClient) {
    this.loadWeeklyDesigner();
  }

  /**
   * Récuperation du designer de la semaine
   */
  loadWeeklyDesigner(): void {
    this.http.get<Designer>(this.apiUrl).subscribe((designer) => {
      this.weeklyDesignerSubject.next(designer);
    });
  }

  /**
   * Méthode utilisée par les composants pour demander le designer de la semaine
   * @returns Le designer associé à l'utilisateur en cours s'il existe sinon null
   */
  getCurrentDesigner(): Designer | null {
    return this.weeklyDesignerSubject.value;
  }
}
