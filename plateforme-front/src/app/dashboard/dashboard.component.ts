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
import { TranslateModule } from '@ngx-translate/core';

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
    MatDialogModule,
    TranslateModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
})
export class DashboardComponent {
  designerForm: FormGroup;
  private subscriptions = new Subscription();
  designerId!: string;

  // Gestion des photos de réalisations
  majorWorks!: String[];
  newMajorWorks!: File[];

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
      biography: [''],
      phoneNumber: [''],
      profession: [''],
      specialties: [[]],
      spheresOfInfluence: [[]],
      favoriteSectors: [[]],
      countryOfOrigin: [''],
      countryOfResidence: [''],
      professionalLevel: [''],
      portfolioUrl: [''],
      realisations: [[]],
      profilePicture: [],
      majorWorks: [[]]
    });
  }

  ngOnInit(): void {
    const userId = this.authService.getUserId();

    if (userId) {
      const sub = this.designerService.getDesignerByUserId(userId).subscribe({
        next: (designer: Designer) => {
          this.designerId = designer.id;
          this.majorWorks = designer.majorWorks;
          this.designerForm.patchValue({
            firstname: designer.firstname,
            lastname: designer.lastname,
            email: designer.email,
            biography: designer.biography,
            phoneNumber: designer.phoneNumber,
            profession: designer.profession,
            specialties: designer.specialties,
            spheresOfInfluence: designer.spheresOfInfluence,
            favoriteSectors: designer.favoriteSectors,
            countryOfOrigin: designer.countryOfOrigin,
            countryOfResidence: designer.countryOfResidence,
            professionalLevel: designer.professionalLevel,
            portfolioUrl: designer.portfolioUrl,
            majorWorks: designer.majorWorks,
            profilePicture: designer.profilePicture,
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
      const updatedDesigner = this.designerForm.value as Designer;

      let country = updatedDesigner.countryOfResidence;
      if (country !== 'USA') {
        country =
          country.charAt(0).toUpperCase() + country.slice(1).toLowerCase();
        updatedDesigner.countryOfResidence = country;
      }

      // Envoi des données au backend
      const sub = this.designerService
        .updateDesignerFields(this.designerId, updatedDesigner)
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
      width: '80%',
      data: { picture: null },
    });

    dialogRef.afterClosed().subscribe((file: File) => {
      if (file) {
        // Mettre à jour la photo de profil avec le fichier sélectionné
        this.designerService
          .updateDesignerPicture(this.designerId, file)
          .subscribe({
            next: (response) => {
              console.log(
                'Photo de profil mise à jour avec succès :',
                response
              );
              this.designerForm.patchValue({
                profilePicture: response.profilePicture,
              }); // Mettez à jour l'affichage
            },
            error: (err) => {
              console.error('Erreur lors de la mise à jour de la photo :', err);
              alert(
                'Une erreur est survenue lors de la mise à jour de la photo.'
              );
            },
          });
      }
    });
  }

  onRealisationSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if(input.files && input.files.length > 0) {
      const fileList = Array.from(input.files);
      const maxSizeMB = 3;

      // Vérifier la taille de chaque fichier selectionné
      for (let file of fileList) {
        if (file.size > maxSizeMB * 1024 * 1024) {
          alert(`Le fichier ${file.name} est trop volumineux. Taille maximale : ${maxSizeMB} Mo.`);
          return;
        }
      }

      this.newMajorWorks = [...fileList]
    }
  }

  updateWorks(): void {
    if (this.newMajorWorks.length > 0) {
      const formData = new FormData();
      this.newMajorWorks.forEach((file) => {
        formData.append('realisations', file, file.name);
      });

      this.designerService.updateMajorWorks(this.designerId, this.newMajorWorks).subscribe({
        next: (response) => {
          this.designerForm.patchValue({
            majorWorks: response.majorWorks,
          });

          alert('Les réalisations ont été mises à jour !');
        },
        error : (err) => {
          alert('Une erreur est survenue lors de la mise à jour des réalisations.')
        }
      })
    }
  }

  getProfilePicture(): string {
    const picture = this.designerForm.get('profilePicture')?.value;
    return picture && picture.trim() !== '' ? picture : 'assets/logos/default-profile.png';
  }

  deleteWork(imageUrl: string): void {
    if (confirm('Voulez-vous vraiment supprimer cette réalisation ?')) {
      this.designerService.deleteMajorWork(this.designerId, imageUrl).subscribe({
        next: (response) => {
          // Mettre à jour les réalisations localement après suppression
          this.designerForm.patchValue({
            majorWorks: response.majorWorks, // Met à jour les réalisations avec celles renvoyées par le backend
          });
          alert('Réalisation supprimée avec succès.');
        },
        error: (err) => {
          console.error('Erreur lors de la suppression de la réalisation :', err);
          alert('Une erreur est survenue lors de la suppression de la réalisation.');
        },
      });
    }
  }
  

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe(); // Nettoie les souscriptions pour éviter les fuites de mémoire
  }
}
