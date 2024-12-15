import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { DesignerService } from '../services/designer.service';
import { AuthService } from '../services/auth.service';
import { Designer } from '../interfaces/designer.interface';
import { Job } from '../enums/job.enum';
import { FavoriteSector } from '../enums/favorite-sector.enum';
import { SphereOfInfluence } from '../enums/sphere-of-influence.enum';
import { Specialty } from '../enums/specialty.enum';
import { PhotoDialogComponent } from '../photo-dialog/photo-dialog.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule,
    MatDialogModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent {
  designerForm: FormGroup;
  private subscriptions = new Subscription();
  designerId!: String;

  // Enums converted to arrays
  specialties = Object.values(Specialty);
  spheresOfInfluence = Object.values(SphereOfInfluence);
  favoriteSectors = Object.values(FavoriteSector);
  jobs = Object.values(Job);

  constructor(
    private readonly designerService: DesignerService,
    private readonly authService: AuthService,
    private readonly fb: FormBuilder,
    private readonly dialog: MatDialog
  ) {
    this.designerForm = this.fb.group({
      firstname: [''],
      lastname: [''],
      email: [''],
      profilePicture: [''],
      biography: [''],
      phoneNumber: [''],
      profession: [''],
      specialties: [[]],
      spheresOfInfluence: [[]],
      favoriteSectors: [[]],
      countryOfOrigin: [''],
      countryOfResidence: [''],
      professionalLevel: [''],
      majorWorks: [[]],
      portfolioUrl: [''],
    });
  }

  ngOnInit(): void {
    const userId = this.authService.getUserIdFromToken();
    console.log("UserId : " + userId);

    if (userId) {
      const sub = this.designerService.getDesignerByUserId(userId).subscribe({
        next: (designer: Designer) => {
          this.designerId = designer.id;
          this.designerForm.patchValue({
            firstname: designer.firstname,
            lastname: designer.lastname,
            email: designer.email,
            profilePicture: designer.profilePicture,
            biography: designer.biography,
            phoneNumber: designer.phoneNumber,
            profession: designer.profession,
            specialties: designer.specialties,
            spheresOfInfluence: designer.spheresOfInfluence,
            favoriteSectors: designer.favoriteSectors,
            countryOfOrigin: designer.countryOfOrigin,
            countryOfResidence: designer.countryOfResidence,
            professionalLevel: designer.professionalLevel,
            majorWorks: designer.majorWorks,
            portfolioUrl: designer.portfolioUrl,
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

  onSubmit(): void {
    if (this.designerForm.valid) {
      const updatedDesigner = this.designerForm.value;
      const sub = this.designerService
        .updateDesigner(updatedDesigner, this.designerId)
        .subscribe({
          next: (response) => {
            console.log('Designer mis à jour avec succès :', response);
            alert('Vos informations ont été mises à jour avec succès.');
          },
          error: (err) => {
            console.error('Erreur lors de la mise à jour :', err);
            alert('Une erreur est survenue lors de la mise à jour.');
          },
        });
      this.subscriptions.add(sub);
    } else {
      console.error('Le formulaire est invalide.');
      alert('Veuillez remplir correctement tous les champs.');
    }
  }

  openPhotoDialog(): void {
    const dialogRef = this.dialog.open(PhotoDialogComponent, {
      width: '400px',
      data: { url: this.designerForm.value.profilePicture },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.designerForm.patchValue({ profilePicture: result });
        console.log('Nouvelle URL de la photo:', result);
      }
    });
  }


  ngOnDestroy(): void {
    this.subscriptions.unsubscribe(); // Nettoie les souscriptions pour éviter les fuites de mémoire
  }
}
