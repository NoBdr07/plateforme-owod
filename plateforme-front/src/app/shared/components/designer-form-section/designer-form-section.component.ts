import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { TranslateModule } from '@ngx-translate/core';
import { Specialty } from '../../enums/specialty.enum';
import { SphereOfInfluence } from '../../enums/sphere-of-influence.enum';
import { FavoriteSector } from '../../enums/favorite-sector.enum';
import { Job } from '../../enums/job.enum';

@Component({
  selector: 'app-designer-form-section',
  standalone: true,
  imports: [
    CommonModule, 
    TranslateModule,
    ReactiveFormsModule, 
    MatFormFieldModule, 
    MatInputModule,
    MatSelectModule,
    MatButtonModule],
  templateUrl: './designer-form-section.component.html',
  styleUrl: './designer-form-section.component.css',
})
export class DesignerFormSectionComponent {
  @Input() form!: FormGroup;
  @Output() submit = new EventEmitter<void>();

  // Enums converted to arrays
    specialties = Object.values(Specialty);
    spheresOfInfluence = Object.values(SphereOfInfluence);
    favoriteSectors = Object.values(FavoriteSector);
    jobs = Object.values(Job);
}
