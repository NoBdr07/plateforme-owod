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

  constructor(private http: HttpClient) {}

  getCompanyByUserId(userId: String): Observable<Company> {
    return this.http.get<Company>(`${this.apiUrl}/by-user/${userId}`);
  }

  createCompany(company: Company): Observable<Company> {
    return this.http.post<Company>(`${this.apiUrl}/new`, company, {
      withCredentials: true
    });
  }


}