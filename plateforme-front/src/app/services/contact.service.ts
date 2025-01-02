import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ContactService {
    private readonly apiUrl = `${environment.apiUrl}`;
    
  constructor(private http: HttpClient) {}

  sendContactEmail(contactData: any) {
    return this.http.post(`${this.apiUrl}/contact`, contactData);
  }
}
