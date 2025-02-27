import { Component, Inject, OnDestroy, OnInit, Renderer2 } from '@angular/core';
import { Designer } from '../interfaces/designer.interface';
import { DesignerEvent } from '../interfaces/designer-event.interface';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { DesignerService } from '../services/designer.service';
import { CommonModule } from '@angular/common';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatListModule } from '@angular/material/list';
import { MatButtonModule } from '@angular/material/button';
import { Subscription } from 'rxjs';

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
  ],
  templateUrl: './calendar-dialog.component.html',
  styleUrl: './calendar-dialog.component.css',
})
export class CalendarDialogComponent implements OnInit, OnDestroy {
  designer!: Designer | undefined;
  selectedDate: Date = new Date();
  eventsOnSelectedDate: DesignerEvent[] = [];
  startAt = new Date();

  subs = new Subscription();

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { designerId: string },
    private designerService: DesignerService,
    private renderer: Renderer2
  ) {}

  ngOnInit(): void {
    this.loadDesignerData();
  }

  loadDesignerData(): void {
    const sub = this.designerService
      .getDesignerById(this.data.designerId)
      .subscribe({
        next: (designer) => {
          this.designer = designer;
          this.startAt = new Date();
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

  dateClass = (date: Date): string => {

    if (!this.designer?.events) return '';

    const isSpecificDate = this.designer.events.some((event) => {
      const eventStartDate = new Date(event.startDate);
      return this.isSameDay(date, eventStartDate);
    });

    const isInPeriod = this.designer.events.some((event) => {
      const eventStartDate = new Date(event.startDate);
      const eventEndDate = new Date(event.endDate);
      return this.isDateInRange(date, eventStartDate, eventEndDate);
    });

    let classes = '';
    if (isSpecificDate) classes += 'event-specific-date ';
    if (isInPeriod) classes += 'event-in-period ';

    return classes.trim();
  };

  private isSameDay(date1: Date, date2: Date): boolean {
    const d1 = new Date(date1.getFullYear(), date1.getMonth(), date1.getDate());
    const d2 = new Date(date2.getFullYear(), date2.getMonth(), date2.getDate());
    return d1.getTime() === d2.getTime();
  }

  private isDateInRange(date: Date, start: Date, end: Date): boolean {
    const d = new Date(date.getFullYear(), date.getMonth(), date.getDate());
    const s = new Date(start.getFullYear(), start.getMonth(), start.getDate());
    const e = new Date(end.getFullYear(), end.getMonth(), end.getDate());
    return d >= s && d <= e;
  }

  onDateSelected(date: Date | null): void {
    if (date) {
      this.selectedDate = date;
      this.updateSelectedDateEvents();
    }
  }

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
