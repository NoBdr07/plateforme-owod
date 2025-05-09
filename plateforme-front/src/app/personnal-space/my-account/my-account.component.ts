import { CommonModule } from '@angular/common';
import {
  Component,
  OnInit,
  OnDestroy,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { UserService } from '../../shared/services/user.service';
import { AuthService } from '../../shared/services/auth.service';
import { filter, Subscription, switchMap, take, tap } from 'rxjs';
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
import { Specialty } from '../../shared/enums/specialty.enum';
import { SphereOfInfluence } from '../../shared/enums/sphere-of-influence.enum';
import { FavoriteSector } from '../../shared/enums/favorite-sector.enum';
import { Job } from '../../shared/enums/job.enum';
import { DesignerService } from '../../shared/services/designer.service';
import { TranslateModule } from '@ngx-translate/core';
import { NotificationService } from '../../shared/services/notifcation.service';
import { Designer } from '../../shared/interfaces/designer.interface';
import { User } from '../../shared/interfaces/user.interface';
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
    MatDialogModule,
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
  @ViewChild('confirmSuppressTemplate')
  confirmSuppressTemplate!: TemplateRef<any>;

  constructor(
    private readonly userService: UserService,
    public authService: AuthService,
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
          console.error(
            "Erreur lors de la récupération de l'utilisateur:",
            err
          );
        },
      });
      this.subscriptions.add(userSub);

      // recup de si l'utilisateur a déjà un compte ou non
      const sub = this.userService
        .hasAnAccount(this.userId)
        .pipe(
          tap((has) => (this.hasAccount = has)),
          filter((has) => has), // on ne poursuit que si true
          switchMap(() =>
            this.designerService.getDesignerByUserId(this.userId)
          ),
          take(1) // pas de multi-émissions
        )
        .subscribe({
          next: (designer) => (this.designerId = designer.id),
          error: (err) => console.log('Designer pas trouvé', err),
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
