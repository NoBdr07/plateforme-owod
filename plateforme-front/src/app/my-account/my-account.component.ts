import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy, TemplateRef, ViewChild } from '@angular/core';
import { UserService } from '../services/user.service';
import { AuthService } from '../services/auth.service';
import { Subscription } from 'rxjs';
import { RouterModule } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Specialty } from '../enums/specialty.enum';
import { SphereOfInfluence } from '../enums/sphere-of-influence.enum';
import { FavoriteSector } from '../enums/favorite-sector.enum';
import { Job } from '../enums/job.enum';
import { DesignerService } from '../services/designer.service';
import { TranslateModule } from '@ngx-translate/core';
import { NotificationService } from '../services/notifcation.service';
import { Designer } from '../interfaces/designer.interface';
import { User } from '../interfaces/user.interface';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';


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
    ReactiveFormsModule,
    TranslateModule,
    MatDialogModule
  ],
  templateUrl: './my-account.component.html',
  styleUrl: './my-account.component.css',
})
export class MyAccountComponent implements OnInit, OnDestroy {

  // Infos de l'utilisateur connecté
  hasAccount: boolean = false;
  designerId!: string;
  userId!: string;
  user: User | null = null;
  accountForm: FormGroup;

  private subscriptions = new Subscription();

  // Enums converted to arrays
  specialties = Object.values(Specialty);
  spheresOfInfluence = Object.values(SphereOfInfluence);
  favoriteSectors = Object.values(FavoriteSector);
  jobs = Object.values(Job);

  // Dialog pour la suppresion de profil
  @ViewChild('confirmSuppressTemplate') confirmSuppressTemplate!: TemplateRef<any>;

  constructor(
    private readonly userService: UserService,
    private readonly authService: AuthService,
    private readonly designerService: DesignerService,
    private readonly fb: FormBuilder,
    private readonly notificationService: NotificationService,
    private readonly dialog: MatDialog
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
    const retrievedUserId = this.authService.getUserId(); // Récupère l'userId
    if (retrievedUserId) {
      this.userId = retrievedUserId;

      // recup du user pour nom prénom
      const userSub = this.userService.getUser(this.userId).subscribe({
        next: (user: User) => {
          this.user = user;
        },
        error: (err) => {
          console.error('Erreur lors de la récupération de l\'utilisateur:', err);
        },
      });
      this.subscriptions.add(userSub);

      // recup de si l'utilisateur a déjà un compte ou non
      const sub = this.userService.hasAnAccount(this.userId).subscribe({
        next: (hasAccount: boolean) => {
          this.hasAccount = hasAccount;
          if (hasAccount === true) {
            this.designerService.getDesignerByUserId(this.userId).subscribe({
              next: (designer: Designer) => {
                this.designerId = designer.id;
              },
              error: (err) => {
                console.log(
                  'Erreur lors de la récupération du designer associé : ' + err
                );
              },
            });
          }
        },
        error: (err) => {
          console.error(
            'Erreur lors de la vérification du compte designer:',
            err
          );
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
      let country = formData.countryOfResidence;
      if (country !== 'USA') {
        country =
          country.charAt(0).toUpperCase() + country.slice(1).toLowerCase();
        formData.countryOfResidence = country;
      }

      // Envoyer la requête pour créer le designer
      const sub = this.designerService.createDesigner(formData).subscribe({
        next: (response) => {
          this.notificationService.success(
            'Profil designer créé avec succès !'
          );
          this.hasAccount = true; // Met à jour le boolean après succès
          this.designerId = response.id;
        },
        error: (err) => {
          console.error('Erreur lors de la création du designer :', err);
        },
      });

      this.subscriptions.add(sub);
    } else {
      console.error('Formulaire invalide.');
    }
  }

  /**
   * Deconnexion de l'utilisateur
   */
  logOut(): void {
    this.authService.logout();
  }

  /**
   * Suppression du profil designer lié à l'utilisateur
   */
  deleteDesigner(): void {
    this.designerService
      .deleteDesigner(this.userId, this.designerId)
      .subscribe({
        next: () => {
          this.hasAccount = false;
          this.designerId = '';
          this.dialog.closeAll();
          this.notificationService.success(
            'Profil designer supprimé avec succès.'
          );
        },
        error: (err) => {
          this.notificationService.error(
            'Une erreur est survenue pendant la suppression.'
          );
        },
      });
  }

  /**
   * Ouverture du dialog de confirmation en cas de suppression du profil designer
   */
  openSuppressDialog(): void {
    this.dialog.open(this.confirmSuppressTemplate);
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
