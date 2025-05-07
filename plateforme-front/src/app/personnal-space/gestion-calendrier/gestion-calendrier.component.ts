import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Designer } from '../../shared/interfaces/designer.interface';
import { DesignerService } from '../../shared/services/designer.service';
import { AuthService } from '../../shared/services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DesignerEvent } from '../../shared/interfaces/designer-event.interface';
import { CommonModule } from '@angular/common';
import { MatError, MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatOptionModule } from '@angular/material/core';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { TranslateModule } from '@ngx-translate/core';
import { RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-gestion-calendrier',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatError,
    MatOptionModule,
    MatCardModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    TranslateModule,
    RouterModule,
  ],
  templateUrl: './gestion-calendrier.component.html',
  styleUrl: './gestion-calendrier.component.css',
})
export class GestionCalendrierComponent implements OnInit, OnDestroy {
  designer!: Designer;
  eventForm!: FormGroup;

  // Pour passer en mode modification d'event plutot qu'ajout
  modifMode = false;
  eventToModify: DesignerEvent | null = null;

  private subs = new Subscription();

  constructor(
    private fb: FormBuilder,
    private designerService: DesignerService,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.designerService.loadDesigners();
    this.loadDesignerData();
  }

  /**
   * Initialisation du formulaire avec valeurs vides
   */
  initForm(): void {
    this.eventForm = this.fb.group({
      title: ['', [Validators.required]],
      description: [''],
      startDate: [new Date(), [Validators.required]],
      endDate: [new Date(), [Validators.required]],
    });
  }

  /**
   * Initialisation du formulaire avec les infos de l'event à modifier
   * @param event 
   */
  initFormForModify(event: DesignerEvent): void {
    this.eventForm = this.fb.group({
      title: [event.title, [Validators.required]],
      description: [event.description],
      startDate: [event.startDate, [Validators.required]],
      endDate: [event.endDate, [Validators.required]],
    });
  }

  /**
   * Chargement des données du designer connecté
   */
  loadDesignerData(): void {
    const userId = this.authService.getUserId();
    if (userId) {
      const sub = this.designerService.getDesignerByUserId(userId).subscribe({
        next: (designer) => {
          if (designer) {
            this.designer = designer;
          }
        },
        error: (err) =>
          console.error(
            'Erreur lors du chargement des données du designer',
            err
          ),
      });

      this.subs.add(sub);
    }
  }

  /**
   * Soumission du formulaire, soit d'ajout soit de modification
   * @returns 
   */
  onSubmit(): void {
    const formValues = this.eventForm.value;

    // Ajuster les dates pour éviter le décalage de fuseau horaire
    if (formValues.startDate) {
      const startDate = new Date(formValues.startDate);
      startDate.setHours(12, 0, 0, 0);
      formValues.startDate = startDate.toISOString();
    }

    if (formValues.endDate) {
      const endDate = new Date(formValues.endDate);
      endDate.setHours(12, 0, 0, 0);
      formValues.endDate = endDate.toISOString();
    }

    if (!this.modifMode) {
      if (this.eventForm.invalid) return;
      const eventData: DesignerEvent = {
        id: this.generateEventId(),
        ...this.eventForm.value,
      };

      this.addEvent(eventData);
    } else {
      if (this.eventForm.invalid) return;
      const eventData: DesignerEvent = {
        id: this.eventToModify?.id,
        ...this.eventForm.value,
      };

      this.modifyEvent(eventData);
    }
  }

  // Generation de l'id de l'event etant donné que ce n'est pas une table à part entiere en base de données
  generateEventId(): string {
    return (
      'event_' + new Date().getTime() + '_' + Math.floor(Math.random() * 1000)
    );
  }

  /**
   * Ajout d'un event
   * @param event 
   */
  addEvent(event: DesignerEvent): void {
    this.designerService.addEvent(event).subscribe({
      next: () => {
        this.loadDesignerData();
        this.initForm();
      },
      error: () => console.log("erreur dans l'ajout de l'evenement"),
    });
  }

  /**
   * Modification du template et du form au passage en mode modification
   * @param event 
   */
  enterModifyEvent(event: DesignerEvent): void {
    this.modifMode = true;
    this.initFormForModify(event);
    this.eventToModify = event;
  }

  /**
   * Modification d'un event
   * @param event 
   */
  modifyEvent(event: DesignerEvent): void {
    if (this.designer) {
      if (confirm('Êtes-vous sûr de vouloir modifier cet événement ?')) {
        this.designerService.modifyEvent(event).subscribe({
          next: () => {
            this.loadDesignerData();
            this.initForm();
            this.modifMode = false;
          },
          error: () => console.log("erreur dans la suppression de l'événement"),
        }
      );
      }
    }
  }
  
  /**
   * Annule la modification et revient en mode ajout
   */
  cancelModify(): void {
    this.initForm();
    this.modifMode = false;
    this.eventToModify = null;
  }

  /**
   * Suppression d'un event
   * @param event 
   */
  deleteEvent(event: DesignerEvent): void {
    if (this.designer) {
      if (confirm('Êtes-vous sûr de vouloir supprimer cet événement ?')) {
        this.designerService.deleteEvent(event).subscribe({
          next: () => {
            this.loadDesignerData();
          },
          error: () => console.log("erreur dans la suppression de l'événement"),
        });
      }
    }
  }

  ngOnDestroy(): void {
      this.subs.unsubscribe();
  }
}
