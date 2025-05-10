import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { TranslateModule } from '@ngx-translate/core';
import { MatIconModule } from '@angular/material/icon';
import {
  catchError,
  combineLatest,
  debounceTime,
  filter,
  map,
  Observable,
  of,
  shareReplay,
  startWith,
} from 'rxjs';
import { Designer } from '../../shared/interfaces/designer.interface';
import { DesignerService } from '../../shared/services/designer.service';
import { MatListModule } from '@angular/material/list';
import { NotificationService } from '../../shared/services/notifcation.service';
import { PhotoDialogComponent } from '../../shared/dialogs/photo-dialog/photo-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { MatOptionModule } from '@angular/material/core';
import { Specialty } from '../../shared/enums/specialty.enum';
import { SphereOfInfluence } from '../../shared/enums/sphere-of-influence.enum';
import { FavoriteSector } from '../../shared/enums/favorite-sector.enum';
import { Job } from '../../shared/enums/job.enum';

@Component({
  selector: 'app-add-designers',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatListModule,
    MatOptionModule,
  ],
  templateUrl: './add-designers.component.html',
  styleUrl: './add-designers.component.css',
})
export class AddDesignersComponent {
  /** Champ de recherche par le nom d'un designer dans la liste */
  searchControl = new FormControl('');

  /** Flux principal des designers crées par l'admin */
  designers$!: Observable<Designer[]>;

  /** Pour afficher un message en cas d'erreur */
  errorMessage: string | null = null;

  /** Mode création pour créer un nouveau designer */
  creationMode: boolean = false;

  /** Enums */
  specialties = Object.values(Specialty);
  spheresOfInfluence = Object.values(SphereOfInfluence);
  favoriteSectors = Object.values(FavoriteSector);
  jobs = Object.values(Job);

  /** Designer en cours si modification */
  designerForm: FormGroup;
  designerId: string | undefined = undefined;

  /** Designer en cours si création */
  accountForm: FormGroup;

  /** Injection des services */
  designerService = inject(DesignerService);
  notificationService = inject(NotificationService);
  dialog = inject(MatDialog);
  fb = inject(FormBuilder);

  constructor() {
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
      majorWorks: [[]],
    });

    this.accountForm = this.fb.group({
      profession: ['', Validators.required],
      specialties: [[], Validators.required],
      spheresOfInfluence: [[], Validators.required],
      favoriteSectors: [[], Validators.required],
      countryOfResidence: ['', Validators.required],
    });
  }

  ngOnInit() {
    // 1) Observable qui charge une fois la liste complète
    const all$ = this.designerService.getDesignersCreatedByAdmin().pipe(
      catchError((err) => {
        console.error(err);
        this.errorMessage = 'Impossible de charger les designers';
        return of([] as Designer[]);
      }),
      // shareReplay permet de ne pas refetch si plusieurs abonnés
      shareReplay({ bufferSize: 1, refCount: true })
    );

    // 2) combineLatest entre la liste et la valeur du champ
    this.designers$ = combineLatest([
      all$,
      this.searchControl.valueChanges.pipe(startWith('')),
    ]).pipe(
      map(([designers, term]) => {
        const q = term?.trim().toLowerCase() || '';
        if (!q) return designers;
        return designers.filter((d) =>
          `${d.firstname} ${d.lastname}`.toLowerCase().includes(q)
        );
      })
    );
  }

  /**
   * Création d'un designer par l'utilisateur admin
   * @param designer
   */
  onCreateDesigner() {
    if (this.accountForm.valid) {
      const formData = this.accountForm.value;
      let country = formData.countryOfResidence;
      if (country !== 'USA') {
        country =
          country.charAt(0).toUpperCase() + country.slice(1).toLowerCase();
        formData.countryOfResidence = country;
      }

      this.designerService.createDesignerAsAdmin(formData).subscribe({
        next: (designers) => {
          this.designers$ = of(designers);
          this.notificationService.success('Designer crée');
          this.creationMode = false;
        },
        error: () => this.notificationService.error('Echec de la création'),
      });
    }
  }

  /**
   * Mode création pour un nouveau designer
   */
  openCreationMode() {
    this.creationMode = true;
  }

  /**
   * Ouverture du dialog pour le chargement de la photo de profil
   */
  /**openPhotoDialog(): void {
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
              this.designerForm.patchValue({
                profilePicture: response.profilePicture,
              });
            },
            error: () => {
              alert(
                'Une erreur est survenue lors de la mise à jour de la photo.'
              );
            },
          });
      }
    });
  }*/
}
