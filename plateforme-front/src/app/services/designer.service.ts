import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, map, Observable, tap } from 'rxjs';
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
   * Méthode pour mise à jour d'un designer
   * @param designer qui contient tout les champs (modifiés ou non)
   * @returns le nouveau designer si sauvegarde en bdd ok
   */
  updateDesigner(designer: Designer, designerId: String): Observable<Designer> {
    return this.http.put<Designer>(`${this.apiUrl}/${designerId}`, designer, {
      withCredentials: true,
    });
  }

  /**
   * Création d'un nouveau designer
   */
  createDesigner(designer: Designer): Observable<Designer> {
    return this.http.post<Designer>(`${this.apiUrl}/new`, designer, {
      withCredentials: true,
    }).pipe(
      tap((newDesigner) => {
        // Recharger la liste des designers après la création
        this.loadDesigners().subscribe();
      })
    );
  }

  /**
   * Récuperation d'un designer à partir du userId
   */
  getDesignerByUserId(userId: string): Observable<Designer> {
    return this.http.get<Designer>(`${this.apiUrl}/designer/${userId}`, {
      withCredentials: true,
    });
  }

  /**
   * Récupération d'un designer spécifique parmi les designers chargés
   * @param designerId ID du designer à récupérer
   * @returns Un observable du designer trouvé
   */
  getDesignerById(designerId: string): Observable<Designer | undefined> {
    return this.designers$.pipe(
      // Transformer la liste en un seul élément correspondant
      map((designers) =>
        designers.find((designer) => designer.id === designerId)
      )
    );
  }

  /**
   * Mise à jour des informations du designer, hors photo et réalisations
   * @param designerId
   * @param updatedDesigner
   * @returns
   */
  updateDesignerFields(
    designerId: string,
    updatedDesigner: Designer
  ): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/${designerId}/update-fields`,
      updatedDesigner,
      {
        withCredentials: true,
      }
    );
  }

  /**
   * Mise à jour de la photo de profil du designer
   * @param designerId
   * @param newPicture
   * @returns
   */
  updateDesignerPicture(designerId: string, newPicture: File): Observable<any> {
    const formData = new FormData();
    formData.append('profilePicture', newPicture);

    return this.http.put(
      `${this.apiUrl}/${designerId}/update-picture`,
      formData,
      {
        withCredentials: true,
      }
    );
  }

  /**
   * Ajout d'une ou plusieurs images de réalisation
   * @param designerId
   * @param realisations
   * @returns
   */
  updateMajorWorks(designerId: string, realisations: File[]): Observable<any> {
    const formData = new FormData();

    realisations.forEach((file) => {
      formData.append('realisations', file, file.name);
    });

    return this.http.put(
      `${this.apiUrl}/${designerId}/update-major-works`,
      formData,
      {
        withCredentials: true,
      }
    );
  }

  /**
   * Suppression d'une réalisation
   * @param designerId
   * @param workUrl
   * @returns
   */
  deleteMajorWork(designerId: string, workUrl: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${designerId}/delete-major-work`, {
      params: { url: workUrl },
      withCredentials: true,
    });
  }


  /**
   * 
   * @param userId 
   * @param designerId 
   * @returns 
   */
  deleteDesigner(userId: string, designerId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${userId}/${designerId}`, {
      withCredentials: true,
      responseType: 'text' 
    }).pipe(
      tap(() => {
        // Recharger la liste des designers après la suppression
        this.loadDesigners().subscribe();
      })
    );
  }

}
