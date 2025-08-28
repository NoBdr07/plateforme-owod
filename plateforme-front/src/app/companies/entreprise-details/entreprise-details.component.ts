import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { Company } from '../../shared/interfaces/company.interface';
import { map, Observable } from 'rxjs';
import { CompanyService } from '../../shared/services/company.service';
import { AuthService } from '../../shared/services/auth.service';

@Component({
  selector: 'app-entreprise-details',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule, MatButtonModule],
  templateUrl: './entreprise-details.component.html',
  styleUrl: './entreprise-details.component.css'
})
export class EntrepriseDetailsComponent {
  company: Company | null = null;

  previewImage: string | null = null; 

  isLogged$: Observable<boolean> = this.authService.session$.pipe(
    map(s => s.isLogged)
  );

  constructor(private route: ActivatedRoute, private companyService: CompanyService, private authService: AuthService) {}


  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.companyService.getById(id).subscribe((data) => {
        if (data) {
          this.company = data;
        }
      })
    }
  }

  /**
   * Ouverture du grossissement d'une réalisation
   * @param imageUrl 
   */
  showPreview(imageUrl: string): void {
    this.previewImage = imageUrl; 
  }

  /**
   * Fermeture du grossissement
   */
  hidePreview(): void {
    this.previewImage = null; 
  }
}
