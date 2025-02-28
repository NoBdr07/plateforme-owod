import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Designer } from '../interfaces/designer.interface';
import { DesignerService } from '../services/designer.service';
import { AuthService } from '../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { DesignerEvent } from '../interfaces/designer-event.interface';
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
    RouterModule
  ],
  templateUrl: './gestion-calendrier.component.html',
  styleUrl: './gestion-calendrier.component.css',
})
export class GestionCalendrierComponent implements OnInit {
  designer!: Designer;
  eventForm!: FormGroup;

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

  initForm(): void {
    this.eventForm = this.fb.group({
      title: ['', [Validators.required]],
      description: [''],
      startDate: [new Date(), [Validators.required]],
      endDate: [new Date(), [Validators.required]],
    });
  }

  loadDesignerData(): void {
    const userId = this.authService.getUserId();
    if (userId) {
      this.designerService.getDesignerByUserId(userId).subscribe({
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
    }
  }

  onSubmit(): void {
    console.log('onSubmit avant return');
    if (this.eventForm.invalid) return;
    console.log('onSubmit apres return');
    const eventData: DesignerEvent = {
      id: this.generateEventId(),
      ...this.eventForm.value,
    };

    this.addEvent(eventData);
  }

  generateEventId(): string {
    return (
      'event_' + new Date().getTime() + '_' + Math.floor(Math.random() * 1000)
    );
  }

  addEvent(event: DesignerEvent): void {
    this.designerService.addEvent(event).subscribe({
      next: () => {
        this.loadDesignerData();
      },
      error: () => console.log("erreur dans l'ajout de l'evenement"),
    });
  }

  deleteEvent(event: DesignerEvent): void {
    if (this.designer) {
      if (confirm('Êtes-vous sûr de vouloir supprimer cet événement ?')) {
        this.designerService.deleteEvent(event).subscribe({
          next: () => {
            this.loadDesignerData();
          },
          error : () => console.log("erreur dans la suppression de l'événement")
        });
      }
    }
  }
}
