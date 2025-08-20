import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NiveauDev } from '../../enums/niveau-dev.enum';
import { TypeEntreprise } from '../../enums/type-entreprise.enum';
import { FavoriteSector } from '../../enums/favorite-sector.enum';
import { CommonModule } from '@angular/common';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-company-form-section',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule
  ],
  templateUrl: './company-form-section.component.html',
  styleUrl: './company-form-section.component.css'
})
export class CompanyFormSectionComponent {
  @Input() form!: FormGroup;
  @Output() formSubmit = new EventEmitter<void>();

  // Enums converted to arrays
  stages = Object.values(NiveauDev);
  companyTypes = Object.values(TypeEntreprise);
  favoriteSectors = Object.values(FavoriteSector);

}
