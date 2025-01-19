import { Component, OnDestroy, OnInit } from '@angular/core';
import { Designer } from '../interfaces/designer.interface';
import {
  BehaviorSubject,
  combineLatest,
  map,
  Observable,
  Subscription,
} from 'rxjs';
import { DesignerService } from '../services/designer.service';
import { CommonModule } from '@angular/common';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-catalogue',
  standalone: true,
  imports: [CommonModule, MatMenuModule, MatButtonModule, RouterModule, TranslateModule],
  templateUrl: './catalogue.component.html',
  styleUrl: './catalogue.component.css',
})
export class CatalogueComponent implements OnInit, OnDestroy {
  designers$!: Observable<Designer[]>;

  // Variables pour les recherches
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

  // Variables pour la pagination
  paginatedDesigners$!: Observable<Designer[]>; // Observable pour les designers paginés
  currentPage = new BehaviorSubject<number>(1);
  maxPage = new BehaviorSubject<number>(1);
  pageSize: number = 50;

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
            return designer.countryOfResidence.trim() == item.trim();
          return true;
        });
      })
    );

    // Calcul du nombre max de page
    this.subs.add(
      this.researchDesigners$.subscribe((filteredDesigners) => {
        const total = filteredDesigners.length;
        const maxPages = Math.ceil(total / this.pageSize);
        this.maxPage.next(maxPages);
      })
    )

    // Pagination
    this.paginatedDesigners$ = combineLatest([
      this.researchDesigners$,
      this.currentPage
    ]).pipe(
      map(([designers, page]) => {
        const startIndex = (page - 1) * this.pageSize;
        const endIndex = startIndex + this.pageSize;
        
        return designers.slice(startIndex, endIndex);
      })
    )
  }

  /**
   * Effectue une recherche dans la liste des designers
   * @param category (pays, secteurs, sphere d'influence, spécialité ou profession)
   * @param item (élément recherché dans la catégorie)
   */
  research(category: string, item: string): void {
    this.researchCriteria.next({ category, item });
    this.currentPage.next(1);
  }

  /**
   * Passe à la pge suivante
   */
  nextPage(): void {
    this.currentPage.next(this.currentPage.value + 1);
  }

  /**
   * Passe à la page précédente
   */
  prevPage(): void {
    if (this.currentPage.value > 1) {
      this.currentPage.next(this.currentPage.value - 1);
    }
  }

  /**
   * Méthode pour extraire des valeurs uniques d'un champ spécifique
   */
  private getUniqueValues<T>(array: T[], key: keyof T): string[] {
    const values: string[] = array
      .map((item) => {
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
