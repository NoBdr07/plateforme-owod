import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { Designer } from '../../shared/interfaces/designer.interface';
import { DesignerEvent } from '../../shared/interfaces/designer-event.interface';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { DesignerService } from '../../shared/services/designer.service';
import { CommonModule } from '@angular/common';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { Subscription } from 'rxjs';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-calendar-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatListModule,
    MatButtonModule,
    TranslateModule,
  ],
  templateUrl: './calendar-dialog.component.html',
  styleUrl: './calendar-dialog.component.css',
})
export class CalendarDialogComponent implements OnInit, OnDestroy {
  // designer connecté
  designer!: Designer | undefined;

  // date séléctionné sur le calendrier
  selectedDate: Date = new Date();
  eventsOnSelectedDate: DesignerEvent[] = [];

  subs = new Subscription();

  /**
   * constructeur avec injection des données liés au designer dont on affiche le calendrier
   * @param data
   * @param designerService
   */
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { designerId: string },
    private designerService: DesignerService
  ) {}

  ngOnInit(): void {
    this.loadDesignerData();
  }

  /**
   * Téléchargement des données liées au designer
   */
  loadDesignerData(): void {
    const sub = this.designerService
      .getDesignerById(this.data.designerId)
      .subscribe({
        next: (designer) => {
          this.designer = designer;
          this.updateSelectedDateEvents();
        },
        error: (err) =>
          console.error(
            'Erreur lors du chargement des données du designer',
            err
          ),
      });

    this.subs.add(sub);
  }

  // Coloration des jours contenant des événements
  dateClass = (date: Date): string => {
    if (!this.designer?.events) return '';

    const isInPeriod = this.designer.events.some((event) => {
      const eventStartDate = new Date(event.startDate);
      const eventEndDate = new Date(event.endDate);
      return this.isDateInRange(date, eventStartDate, eventEndDate);
    });

    let classes = '';
    if (isInPeriod) classes += 'event-in-period ';

    return classes.trim();
  };

  /**
   * Check si une date est entre deux dates
   * @param date 
   * @param start 
   * @param end 
   * @returns un boolean
   */
  private isDateInRange(date: Date, start: Date, end: Date): boolean {
    const d = new Date(date.getFullYear(), date.getMonth(), date.getDate());
    const s = new Date(start.getFullYear(), start.getMonth(), start.getDate());
    const e = new Date(end.getFullYear(), end.getMonth(), end.getDate());
    return d >= s && d <= e;
  }

  /**
   * Quand l'utilisateur selectionne une date dans le calendrier
   * @param date 
   */
  onDateSelected(date: Date | null): void {
    if (date) {
      this.selectedDate = date;
      this.updateSelectedDateEvents();
    }
  }

  /**
   * Selection des evenements du jour
   * @returns 
   */
  updateSelectedDateEvents(): void {
    if (!this.designer?.events) {
      this.eventsOnSelectedDate = [];
      return;
    }

    this.eventsOnSelectedDate = this.designer.events.filter((event) =>
      this.isDateInRange(
        this.selectedDate,
        new Date(event.startDate),
        new Date(event.endDate)
      )
    );
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
