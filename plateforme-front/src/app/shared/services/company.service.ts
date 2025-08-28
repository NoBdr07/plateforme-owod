import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { Company } from "../interfaces/company.interface";

@Injectable({
  providedIn: 'root',
})
export class CompanyService {

  private apiUrl = `${environment.apiUrl}/company`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<Company[]> {
    return this.http.get<Company[]>(`${this.apiUrl}/all`);
  }

  getById(companyId: string): Observable<Company> {
    return this.http.get<Company>(`${this.apiUrl}/${companyId}`);
  }

  getFullById(companyId: string): Observable<Company> {
    return this.http.get<Company>(`${this.apiUrl}/${companyId}/full`, { withCredentials: true });
  }

  getCompanyByUserId(userId: string): Observable<Company> {
    return this.http.get<Company>(`${this.apiUrl}/by-user/${userId}`, { withCredentials: true });
  }

  createCompany(company: Company): Observable<Company> {
    return this.http.post<Company>(`${this.apiUrl}/new`, company, {
      withCredentials: true
    });
  }

  updateCompanyFields(companyId: string, patch: Partial<Company>): Observable<Company> {
    return this.http.patch<Company>(`${this.apiUrl}/${companyId}`, patch, { withCredentials: true });
  }

  /** Upload logo */
  updateLogo(companyId: string, file: File): Observable<Company> {
    const fd = new FormData();
    fd.append('file', file);
    return this.http.post<Company>(`${this.apiUrl}/${companyId}/logo`, fd, { withCredentials: true });
  }

  /** Upload team photo */
  updateTeamPhoto(companyId: string, file: File): Observable<Company> {
    const fd = new FormData();
    fd.append('file', file);
    return this.http.post<Company>(`${this.apiUrl}/${companyId}/team-photo`, fd, { withCredentials: true });
  }

  /** Ajout works (multi-fichiers) */
  addWorks(companyId: string, files: File[]): Observable<Company> {
    const fd = new FormData();
    for (const f of files) fd.append('files', f);
    return this.http.post<Company>(`${this.apiUrl}/${companyId}/works`, fd, { withCredentials: true });
  }

  /** Suppression d’un work par URL */
  deleteWork(companyId: string, url: string): Observable<Company> {
    return this.http.delete<Company>(`${this.apiUrl}/${companyId}/works`, {
      withCredentials: true,
      params: { url }
    });
  }

  /** Suppression du compte entreprise */
  deleteCompany(companyId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${companyId}`, {
      withCredentials: true,
      responseType: 'text',
    })
  }


}