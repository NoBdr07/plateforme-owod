import { Component, OnDestroy, OnInit } from '@angular/core';
import { Designer } from '../interfaces/designer.interface';
import { BehaviorSubject, combineLatest, filter, map, Observable, Subscription } from 'rxjs';
import { DesignerService } from '../services/designer.service';
import { CommonModule } from '@angular/common';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-catalogue',
  standalone: true,
  imports: [CommonModule, MatMenuModule, MatButtonModule, RouterModule],
  templateUrl: './catalogue.component.html',
  styleUrl: './catalogue.component.css',
})
export class CatalogueComponent implements OnInit, OnDestroy {
  designers$!: Observable<Designer[]>;
  researchDesigners$!: Observable<Designer[]>;
  private researchCriteria = new BehaviorSubject<{
    category: string;
    item: string;
  } | null>(null);

  // Listes des specialités, pays, spheres et secteurs présents pour les listes de recherche
  specialties!: string[];
  countries!: string[];
  spheres!: string[];
  sectors!: string[];

  subs = new Subscription();

  constructor(private readonly designerService: DesignerService) {}

  /**
   * Initialisation de la liste des designers, des listes servant aux recherches parmi les designers
   */
  ngOnInit(): void {
    this.subs.add(
      this.designerService.loadDesigners().subscribe((designers) => {
        this.specialties = this.getUniqueValues(designers, 'specialties');
        this.spheres = this.getUniqueValues(designers, 'spheresOfInfluence');
        this.sectors = this.getUniqueValues(designers, 'favoriteSectors');
        this.countries = this.getUniqueValues(designers, 'countryOfResidence');
      })
    );

    this.designers$ = this.designerService.getDesigners();

    this.researchDesigners$ = combineLatest([
      this.designers$,
      this.researchCriteria,
    ]).pipe(
      map(([designers, criteria]) => {
        if (!criteria) return designers; // Retourne tous les designers si aucun filtre
        const { category, item } = criteria;

        return designers.filter((designer) => {
          if (category === 'profession') return designer.profession === item;
          if (category === 'specialty')
            return designer.specialties.includes(item);
          if (category === 'sphere')
            return designer.spheresOfInfluence.includes(item);
          if (category === 'sector')
            return designer.favoriteSectors.includes(item);
          if (category === 'country')
            return designer.countryOfResidence === item;
          return true;
        });
      })
    );
  }

  /**
   * Effectue une recherche dans la liste des designers
   * @param category (pays, secteurs, sphere d'influence, spécialité ou profession)
   * @param item (élément recherché dans la catégorie)
   */
  research(category: string, item: string): void {
    this.researchCriteria.next({ category, item });
  }

  /**
   * Méthode pour extraire des valeurs uniques d'un champ spécifique
   */
  private getUniqueValues<T>(array: T[], key: keyof T): string[] {
    const values: string[] = array
      .map(item => {
        const val = item[key];
        // Vérifie si val est un tableau, sinon convertit en tableau
        return Array.isArray(val) ? val : [val];
      })
      .flat() as string[]; // Cast pour s'assurer que les valeurs sont des strings
  
    return [...new Set(values)];
  }
  

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
