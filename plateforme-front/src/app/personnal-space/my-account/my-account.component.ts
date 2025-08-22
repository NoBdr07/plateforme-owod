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
import { TypeEntreprise } from '../../shared/enums/type-entreprise.enum';
import { NiveauDev } from '../../shared/enums/niveau-dev.enum';
import { CompanyService } from '../../shared/services/company.service';

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

  // Formulaire pour création du compte designer ou entreprise
  accountForm: FormGroup;
  companyForm: FormGroup;

  // Pour l'upload du logo si création de compte entreprise
  companyLogoName: string | null = null;

  // Enum des accountType pour qu'ils soient dispo dans le template
  public AccountType = AccountType;

  // Enums converted to arrays
  specialties = Object.values(Specialty);
  spheresOfInfluence = Object.values(SphereOfInfluence);
  favoriteSectors = Object.values(FavoriteSector);
  jobs = Object.values(Job);
  companyTypes = Object.values(TypeEntreprise);

  // Dialog pour la suppresion de profil
  @ViewChild('confirmSuppressTemplate')
  confirmSuppressTemplate!: TemplateRef<any>;

  private subs = new Subscription();

  constructor(
    private readonly userService: UserService,
    public authService: AuthService,
    private readonly designerService: DesignerService,
    private readonly companyService: CompanyService,
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

    this.companyForm = this.fb.group({
      logo: ['', Validators.required],
      raisonSociale: ['', Validators.required],
      sectors: [[], Validators.required],
      type: ['', Validators.required],
      country: ['', Validators.required],
      city: ['', Validators.required],
      websiteUrl: ['']
    })
  }

  onSubmitDesigner(): void {
    if (this.accountForm.valid) {
      const formData = this.accountForm.value;
      let country = formData.countryOfResidence;
      formData.countryOfResidence = this.formatCountry(country);


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

      this.subs.add(sub);

    } else {
      console.error('Formulaire invalide.');
    }
  }

  onSubmitCompany(): void {
    if (this.companyForm.invalid) return;

    const formData = this.companyForm.value;
    let country = formData.country;
    formData.country = this.formatCountry(country);

    const logoFile: File = this.companyForm.get('logo')?.value;

    // envoyer requête pour création du compte entreprise
    const sub = this.companyService.createCompany(formData).subscribe({
      next: (created) => {

        const sub2 = this.companyService.updateLogo(created.id, logoFile).subscribe({
          next: () => {
            this.notificationService.success('Compte entreprise créé avec succès.');
            this.subs.add(this.authService.refreshSession().subscribe());
            this.subs.add(sub);
            this.subs.add(sub2);
          },
          error: (err) => {
            this.notificationService.error('Erreur lors de la création du compte.');
            console.error(err);
          }
        })

      },
      error: (err) => {
        console.error('Erreur lors de la création du compte entreprise : ', err);
        this.notificationService.error('Erreur lors de la création du compte.');
      }
    })
  }

  /**
   * Formattage des pays
   * 
   * @param country 
   * @returns 
   */
  formatCountry(country: String): String {
    if (country !== 'USA') {
      country = country.charAt(0).toUpperCase() + country.slice(1).toLowerCase();
    }
    return country;
  }

  /**
   * Relie le fichier image du logo au form de création d'entreprise
   * 
   * @param event qui contient le fichier uploadé
   */
  onLogoSelected(event: Event): void {
    console.log("appel de onLogoSelected");
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      console.log("if dans onLogoSelected");
      const file = input.files[0];
      this.companyForm.get('logo')?.setValue(file);
      this.companyForm.get('logo')?.updateValueAndValidity();
      this.companyLogoName = file.name;
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
  deleteAccount(): void {

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
