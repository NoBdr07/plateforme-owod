import { Injectable } from "@angular/core";
import { environment } from "../../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { Company } from "../interfaces/company.interface";

@Injectable({
  providedIn: 'root',
})
export class CompanyService {

  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  getCompanyByUserId(userId: String): Observable<Company> {
    return this.http.get<Company>(`${this.apiUrl}/by-user/${userId}`);
  }

}