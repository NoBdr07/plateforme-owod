import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PasswordResetService {
  private apiUrl = `${environment.apiUrl}/password`;

  constructor(private readonly http: HttpClient) {}

  requestReset(email: string) {
    return this.http.post(`${this.apiUrl}/request-reset`, email);
  }

  resetPassword(token: string, newPassword: string) {
    return this.http.post(`${this.apiUrl}/reset`, { token, newPassword });
  }
}
