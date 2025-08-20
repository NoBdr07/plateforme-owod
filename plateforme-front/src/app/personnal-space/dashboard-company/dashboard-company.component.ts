import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { DesignerService } from '../../shared/services/designer.service';
import { AuthService } from '../../shared/services/auth.service';
import { Designer } from '../../shared/interfaces/designer.interface';
import { Job } from '../../shared/enums/job.enum';
import { FavoriteSector } from '../../shared/enums/favorite-sector.enum';
import { SphereOfInfluence } from '../../shared/enums/sphere-of-influence.enum';
import { Specialty } from '../../shared/enums/specialty.enum';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { PhotoSectionComponent } from '../../shared/components/photo-section/photo-section.component';
import { MajorWorksSectionComponent } from '../../shared/components/major-works-section/major-works-section.component';
import { CompanyService } from '../../shared/services/company.service';
import { Company } from '../../shared/interfaces/company.interface';
import { CompanyFormSectionComponent } from '../../shared/components/company-form-section/company-form-section.component';


@Component({
  selector: 'app-dashboard-company',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule,
    MatDialogModule,
    TranslateModule,
    PhotoSectionComponent,
    MajorWorksSectionComponent,
    CompanyFormSectionComponent
  ],
  templateUrl: './dashboard-company.component.html',
  styleUrl: './dashboard-company.component.css'
})
export class DashboardCompanyComponent {
  companyForm: FormGroup;
  private subscriptions = new Subscription();
  companyId!: string;
  company: Company | null = null;

  // Gestion des photos de réalisations
  newMajorWorks: File[] = [];

  constructor(
    private readonly companyService: CompanyService,
    private readonly authService: AuthService,
    private readonly fb: FormBuilder,
    private readonly dialog: MatDialog
  ) {
    this.companyForm = this.fb.group({
      raisonSociale: [''],
      siretNumber: [''],
      phoneNumber: [''],
      email: [''],
      description: [''],
      stage: [''],
      type: [''],
      sectors: [[]],
      country: [''],
      city: [''],
      revenue: [''],
      websiteUrl: [''],
      worksUrl: [[]],
      logoUrl: [],
      teamPhotoUrl: [],
      financialSupport: [false]
    });
  }

  ngOnInit(): void {
    const userId = this.authService.getUserId();

    // Récuperation des infos du designer et initialisation du form avec ses infos
    if (userId) {
      const sub = this.companyService.getCompanyByUserId(userId).subscribe({
        next: (company: Company) => {
          this.company = company;
          this.companyId = company.id;
          
          this.companyForm.patchValue({
            raisonSociale: company.raisonSociale,
            siretNumber: company.siretNumber,
            phoneNumber: company.phoneNumber,
            email: company.email,
            description: company.description,
            stage: company.stage,
            type: company.type,
            sectors: company.sectors,
            country: company.country,
            city: company.city,
            revenue: company.revenue,
            websiteUrl: company.websiteUrl ?? '',
            logoUrl: company.logoUrl ?? '',
            teamPhotoUrl: company.teamPhotoUrl ?? '',
            financialSupport: company.financialSupport ?? false
          });
        },
        error: (err) => {
          console.error(
            'Erreur lors de la récupération des infos designer:',
            err
          );
        },
      });

      this.subscriptions.add(sub);
    } else {
      console.error('Dashboard : Utilisateur non authentifié.');
    }
  }

  /**
   * Soumission du formulaire des infos utilisateurs
   */
  onSubmit(): void {
    if (this.companyForm.valid) {
      const patch: Partial<Company> = {...this.companyForm.value};

      let country = patch.country;
      if (country && country !== 'USA') {
        country =
          country.charAt(0).toUpperCase() + country.slice(1).toLowerCase();
        patch.country = country;
      }

      // Envoi des données au backend
      const sub = this.companyService
        .updateCompanyFields(this.companyId, patch)
        .subscribe({
          next: (updated) => {
            this.company = updated;
            this.companyForm.patchValue({
              worksUrl: updated.worksUrl ?? [],
              logoUrl: updated.logoUrl ?? '',
              teamPhotoUrl: updated.teamPhotoUrl ?? ''
            })
            alert('Vos informations ont été mises à jour avec succès.');
          },
          error: () => {
            alert('Une erreur est survenue lors de la mise à jour.');
          },
        });

      this.subscriptions.add(sub);
    } else {
      alert('Veuillez remplir correctement tous les champs.');
    }
  }

  // logo
onLogoSelected(file: File) {
  this.companyService.updateLogo(this.companyId, file).subscribe({
    next: (updated) => {
      this.company = updated;
      this.companyForm.patchValue({
        logoUrl: updated.logoUrl ?? ''
      });
    },
    error: () => alert('Erreur lors de la mise à jour du logo')
  });
}

// team photo
onTeamPictureSelected(file: File) {
    if (!this.companyId) return;
    this.companyService.updateTeamPhoto(this.companyId, file).subscribe({
      next: (updated) => {
        this.company = updated;
        this.companyForm.patchValue({ teamPhotoUrl: updated.teamPhotoUrl ?? '' });
      },
      error: () => alert("Erreur lors de la mise à jour de la photo d'équipe")
    });
  }

  /**
   * Verification de la taille du fichier que l'utilisateur upload pour ses réalisations
   * @param event 
   * @returns 
   */
  onRealisationSelected(files: FileList): void {
    if (!files?.length) return;
    const fileList = Array.from(files);
    const maxSizeMB = 3;
    for (const f of fileList) {
      if (f.size > maxSizeMB * 1024 * 1024) {
        alert(`Le fichier ${f.name} est trop volumineux. Taille maximale : ${maxSizeMB} Mo.`);
        return;
      }
    }
    this.newMajorWorks = fileList;
  }

  /**
   * Mise à jour des réalisations du designer
   */
  updateWorks(): void {
    if (!this.companyId || !this.newMajorWorks.length) return;

    this.companyService.addWorks(this.companyId, this.newMajorWorks).subscribe({
      next: (updated) => {
        this.company = updated;
        this.companyForm.patchValue({ worksUrl: updated.worksUrl ?? [] });
        this.newMajorWorks = [];
        alert('Les réalisations ont été mises à jour !');
      },
      error: () => alert('Erreur lors de la mise à jour des réalisations.')
    });
  }

  /**
   * Suppression d'un fichier de réalisation
   * @param imageUrl 
   */
  deleteWork(imageUrl: string): void {
    if (!this.companyId) return;

    if (confirm('Voulez-vous vraiment supprimer cette réalisation ?')) {
      this.companyService.deleteWork(this.companyId, imageUrl).subscribe({
        next: (updated) => {
          this.company = updated;
          this.companyForm.patchValue({ worksUrl: updated.worksUrl ?? [] });
          alert('Réalisation supprimée avec succès.');
        },
        error: () => alert('Une erreur est survenue lors de la suppression de la réalisation.')
      });
    }
  }


  ngOnDestroy(): void {
    this.subscriptions.unsubscribe(); // Nettoie les souscriptions pour éviter les fuites de mémoire
  }
}
