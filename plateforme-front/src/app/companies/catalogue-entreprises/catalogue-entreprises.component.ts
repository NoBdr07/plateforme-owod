import {
  Component,
  OnInit,
  OnDestroy
} from '@angular/core';
import {
  BehaviorSubject,
  combineLatest,
  map,
  Observable,
  Subscription,
  tap,
} from 'rxjs';
import { CommonModule } from '@angular/common';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { MatTooltipModule, TooltipPosition } from '@angular/material/tooltip';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { CompanyService } from '../../shared/services/company.service';
import { Company } from '../../shared/interfaces/company.interface';


@Component({
  selector: 'app-catalogue-entreprises',
  standalone: true,
  imports: [
    CommonModule,
    MatMenuModule,
    MatTooltipModule,
    MatButtonModule,
    RouterModule,
    TranslateModule,
    MatDialogModule,
  ],
  templateUrl: './catalogue-entreprises.component.html',
  styleUrl: './catalogue-entreprises.component.css'
})
export class CatalogueEntreprisesComponent implements OnInit, OnDestroy {

  // Données brutes & filtrées
  companies$!: Observable<Company[]>;
  researchCompanies$!: Observable<Company[]>;
  paginatedCompanies$!: Observable<Company[]>;

  // Critère de recherche
  private researchCriteria = new BehaviorSubject<{ category: 'type' | 'sector' | 'country'; item: string } | null>(null);

  // Listes pour les filtres
  sectors!: string[];
  types!: string[];
  countries!: string[];

  // Pagination
  currentPage = new BehaviorSubject<number>(1);
  maxPage = new BehaviorSubject<number>(1);
  pageSize: number = 50;

  // Souscriptions
  subs = new Subscription();

  constructor(private readonly companyService: CompanyService) { }

  ngOnInit(): void {
    // Récuperation des entreprises
    this.companies$ = this.companyService.getAll();

    // Construire les listes de filtres à partir des données
    this.subs.add(
      this.companies$.subscribe((companies) => {
        this.sectors = this.getUniqueValues(companies, 'sectors');
        this.types = this.getUniqueValues(companies, 'type');
        this.countries = this.getUniqueValues(companies, 'country');
      })
    );

    // Flux des entreprises filtrées
    this.researchCompanies$ = combineLatest([this.companies$, this.researchCriteria]).pipe(
      map(([companies, criteria]) => {
        if (!criteria) return companies;

        const item = (criteria.item ?? '').trim().toLowerCase();

        return companies.filter((c) => {
          if (criteria.category === 'type') {
            return (c.type ?? '').trim().toLowerCase() === item;
          }
          if (criteria.category === 'sector') {
            const list = (c.sectors ?? []).map((s) => (s ?? '').trim().toLowerCase());
            return list.includes(item);
          }
          if (criteria.category === 'country') {
            return (c.country ?? '').trim().toLowerCase() === item;
          }
          return true;
        });
      })
    );

    // Calcul du nombre max de pages
    this.subs.add(
      this.researchCompanies$.subscribe((filtered) => {
        const total = filtered.length;
        const maxPages = Math.max(1, Math.ceil(total / this.pageSize));
        this.maxPage.next(maxPages);

        // Sécurité : si le filtre réduit le nb de pages, on remet sur la 1ère page
        if (this.currentPage.value > maxPages) {
          this.currentPage.next(1);
        }
      })
    );

    // Pagination
    this.paginatedCompanies$ = combineLatest([this.researchCompanies$, this.currentPage]).pipe(
      map(([companies, page]) => {
        const startIndex = (page - 1) * this.pageSize;
        const endIndex = startIndex + this.pageSize;
        return companies.slice(startIndex, endIndex);
      })
    );
  }

ngOnDestroy(): void {
  this.subs.unsubscribe();
}

  /**
   * Méthode pour extraire des valeurs uniques d'un champ spécifique
   */
  private getUniqueValues<T>(array: T[], key: keyof T): string[] {
  const values: string[] = array
    .map((item) => {
      const val = item[key];
      // Vérifie si val est un tableau, sinon convertit en tableau
      return Array.isArray(val)
        ? val.map((v) => (typeof v === 'string' ? v.trim() : v))
        : [typeof val === 'string' ? val.trim() : val];
    })
    .flat() as string[];

  return [...new Set(values)];
}

// Déclenche un filtrage
  research(category: 'type' | 'sector' | 'country', item: string): void {
    this.researchCriteria.next({ category, item });
    this.currentPage.next(1);
  }

/**
 * Revenir a la liste de designers complète
 */
resetResearch(): void {
  this.researchCriteria.next(null);
  this.currentPage.next(1);
}

/**
 * Passe à la page suivante
 */
nextPage(): void {
  this.currentPage.next(this.currentPage.value + 1);
}

/**
 * Passe à la page précédente
 */
prevPage(): void {
  if(this.currentPage.value > 1) {
  this.currentPage.next(this.currentPage.value - 1);
}
  }

}
