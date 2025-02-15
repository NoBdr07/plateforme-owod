import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { ContactData } from '../interfaces/contact-data.interface';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ContactService {
    private readonly apiUrl = `${environment.apiUrl}`;
    
  constructor(private http: HttpClient) {}

  /**
   * Envoie la requête http pour l'envoi du mail suite à la soumission du formulaire de contact
   * @param contactData qui contient email, sujet, raison, description
   */
  sendContactEmail(contactData: ContactData): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/contact`, contactData);
  }
}
