import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { UserService } from '../services/user.service';
import { AuthService } from '../services/auth.service';
import { Subscription } from 'rxjs';
import { RouterModule } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select'
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Specialty } from '../enums/specialty.enum';
import { SphereOfInfluence } from '../enums/sphere-of-influence.enum';
import { FavoriteSector } from '../enums/favorite-sector.enum';
import { Job } from '../enums/job.enum';
import { DesignerService } from '../services/designer.service';

@Component({
  selector: 'app-my-account',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
    MatSelectModule,
    ReactiveFormsModule
  ],
  templateUrl: './my-account.component.html',
  styleUrl: './my-account.component.css',
})
export class MyAccountComponent implements OnInit, OnDestroy {
  hasAccount: boolean = false;
  accountForm: FormGroup;
  private subscriptions = new Subscription();

  // Enums converted to arrays
  specialties = Object.values(Specialty);
  spheresOfInfluence = Object.values(SphereOfInfluence);
  favoriteSectors = Object.values(FavoriteSector);
  jobs = Object.values(Job);

  constructor(
    private readonly userService: UserService,
    private readonly authService: AuthService,
    private readonly designerService: DesignerService,
    private readonly fb: FormBuilder
  ) {
    this.accountForm = this.fb.group({
      profession: ['', Validators.required],
      specialties: [[], Validators.required],
      spheresOfInfluence: [[], Validators.required],
      favoriteSectors: [[], Validators.required],
      countryOfResidence: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    const userId = this.authService.getUserIdFromToken(); // Récupère l'userId
    if (userId) {
      const sub = this.userService.hasAnAccount(userId).subscribe({
        next: (hasAccount: boolean) => {
          this.hasAccount = hasAccount;
        },
        error: (err) => {
          console.error('Erreur lors de la vérification du compte designer:', err);
        },
      });
      this.subscriptions.add(sub);
    } else {
      console.error('Utilisateur non authentifié.');
    }
  }

  onSubmit(): void {
    if (this.accountForm.valid) {
      const formData = this.accountForm.value;

      // Envoyer la requête pour créer le designer
      const sub = this.designerService.createDesigner(formData).subscribe({
        next: (response) => {
          console.log('Designer créé avec succès :', response);
          this.hasAccount = true; // Met à jour le boolean après succès
        },
        error: (err) => {
          console.error('Erreur lors de la création du designer :', err);
        },
      });

      this.subscriptions.add(sub); // Ajouter la souscription pour nettoyage ultérieur
    } else {
      console.error('Formulaire invalide.');
    }
  }

  logOut(): void {
    this.authService.logout();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe(); // Nettoyer les subscriptions
  }
}