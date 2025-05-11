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
  Subscription,
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
import { DesignerFormSectionComponent } from '../../shared/components/designer-form-section/designer-form-section.component';
import { MajorWorksSectionComponent } from '../../shared/components/major-works-section/major-works-section.component';
import { PhotoSectionComponent } from '../../shared/components/photo-section/photo-section.component';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { RouterModule } from '@angular/router';
import { TransferDialogComponent } from '../transfer-dialog/transfer-dialog.component';

@Component({
  selector: 'app-add-designers',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatSelectModule,
    MatIconModule,
    MatListModule,
    MatOptionModule,
    MatInputModule,
    RouterModule,
    DesignerFormSectionComponent,
    MajorWorksSectionComponent,
    PhotoSectionComponent,
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
  designerId: string = '';
  majorWorks!: String[];
  newMajorWorks!: File[];

  /** Designer en cours si création */
  accountForm: FormGroup;

  /* Souscriptions */
  subscriptions = new Subscription();

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
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
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
    // Création du designer
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
   * Selection d'un designer à gérer / modifier
   */
  onSelectDesigner(designer: Designer) {
    this.creationMode = false;
    this.designerId = designer.id;
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
  }

  /**
   * Mode création pour un nouveau designer
   */
  openCreationMode() {
    // Remise à 0 du designerId pour fermer le mode modification
    this.designerId = '';
    this.creationMode = true;    
  }

  /**
   * Ouverture du dialog pour le chargement de la photo de profil
   */
  openPhotoDialog(): void {
    const dialogRef = this.dialog.open(PhotoDialogComponent, {
      width: '80%',
      data: { picture: null },
    });

    if (this.designerId) {
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
    }
  }

  /**
   * Récuperation de la photo de profil ou photo de default si pas de photo de profil
   * @returns
   */
  getProfilePicture(): string {
    const picture = this.designerForm.get('profilePicture')?.value;
    return picture && picture.trim() !== ''
      ? picture
      : 'assets/logos/default-profile.png';
  }

  /**
   * Mise à jour des réalisations du designer
   */
  updateWorks(): void {
    if (this.newMajorWorks.length > 0) {
      const formData = new FormData();
      this.newMajorWorks.forEach((file) => {
        formData.append('realisations', file, file.name);
      });

      this.designerService
        .updateMajorWorks(this.designerId, this.newMajorWorks)
        .subscribe({
          next: (response) => {
            this.designerForm.patchValue({
              majorWorks: response.majorWorks,
            });

            alert('Les réalisations ont été mises à jour !');
          },
          error: (err) => {
            alert(
              'Une erreur est survenue lors de la mise à jour des réalisations.'
            );
          },
        });
    }
  }

  /**
   * Verification de la taille du fichier que l'utilisateur upload pour ses réalisations
   * @param event
   * @returns
   */
  onRealisationSelected(files: FileList): void {
    if (files && files.length > 0) {
      const fileList = Array.from(files);
      const maxSizeMB = 3;

      // Vérifier la taille de chaque fichier selectionné
      for (let file of fileList) {
        if (file.size > maxSizeMB * 1024 * 1024) {
          alert(
            `Le fichier ${file.name} est trop volumineux. Taille maximale : ${maxSizeMB} Mo.`
          );
          return;
        }
      }

      this.newMajorWorks = [...fileList];
    }
  }

  /**
   * Suppression d'un fichier de réalisation
   * @param imageUrl
   */
  deleteWork(imageUrl: string): void {
    if (confirm('Voulez-vous vraiment supprimer cette réalisation ?')) {
      this.designerService
        .deleteMajorWork(this.designerId, imageUrl)
        .subscribe({
          next: (response) => {
            // Mettre à jour les réalisations localement après suppression
            this.designerForm.patchValue({
              majorWorks: response.majorWorks, // Met à jour les réalisations avec celles renvoyées par le backend
            });
            alert('Réalisation supprimée avec succès.');
          },
          error: () => {
            alert(
              'Une erreur est survenue lors de la suppression de la réalisation.'
            );
          },
        });
    }
  }

  /**
   * Soumission du formulaire des infos utilisateurs
   */
  onModifyDesigner(): void {
    this.creationMode = false;
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
          next: () => {
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

  /**
   * Suppression d'un designer crée par l'admin
   * @param designerId
   */
  deleteDesigner(designerId: string) {
    if (
      confirm(
        'Voulez vous vraiment supprimer ce designer ? Cette action est irreversible.'
      )
    ) {
      this.designerService.deleteCreatedDesignerAsAdmin(designerId).subscribe({
        next: () => {
          alert('Designer supprimé');
        },
        error: () => {
          alert('Une erreur est survenue lors de la suppression du designer.');
        },
      });
    }
  }

  /**
   * Ouvre le dialog pour le transfert d'un designer
   */
  openTransfer(designerId: string) {
    const dialogRef = this.dialog.open(TransferDialogComponent, {
      width: '500px',
      data: { designerId },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.userId) {
        this.designerService
          .transferDesigner(result.userId, designerId)
          .subscribe({
            next: () => {
              alert('Designer transféré');
            },
            error: () => {
              alert('Erreur lors du transfert.')
            }
          });
      }
    });
  }

  /**
   * Nettoie les souscriptions pour éviter les fuites de mémoire
   */
  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
