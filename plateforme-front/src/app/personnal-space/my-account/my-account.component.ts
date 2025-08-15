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
import { AccountType } from '../../shared/enums/account-type.enum';
import { MatTabsModule } from '@angular/material/tabs';

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
    MatTabsModule
  ],
  templateUrl: './my-account.component.html',
  styleUrl: './my-account.component.css',
})
export class MyAccountComponent implements OnDestroy {
  // Infos de l'utilisateur connecté 
  session$ = this.authService.session$;

  // Formulaire pour création du compte designer
  accountForm: FormGroup;

  // Enum des accountType pour qu'ils soient dispo dans le template
  public AccountType = AccountType;

  // Enums converted to arrays
  specialties = Object.values(Specialty);
  spheresOfInfluence = Object.values(SphereOfInfluence);
  favoriteSectors = Object.values(FavoriteSector);
  jobs = Object.values(Job);

  // Dialog pour la suppresion de profil
  @ViewChild('confirmSuppressTemplate')
  confirmSuppressTemplate!: TemplateRef<any>;

  private subs = new Subscription();

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

  onSubmitDesigner(): void {
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
          this.subs.add(this.authService.refreshSession().subscribe());
        },
        error: (err) => {
          console.error('Erreur lors de la création du designer :', err);
        },
      });

    } else {
      console.error('Formulaire invalide.');
    }
  }

  onSubmitCompany(): void {

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
  }

  /**
   * Ouverture du dialog de confirmation en cas de suppression du profil designer
   */
  openSuppressDialog(): void {
    this.dialog.open(this.confirmSuppressTemplate);
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
