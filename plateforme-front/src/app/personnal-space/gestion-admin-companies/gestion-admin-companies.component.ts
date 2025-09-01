import { CommonModule } from '@angular/common';
import { Component, ViewChild, AfterViewInit, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { CompanyService } from '../../shared/services/company.service';
import { Company } from '../../shared/interfaces/company.interface';

@Component({
  selector: 'app-gestion-admin-companies',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    TranslateModule,
    // Angular Material
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule
  ],
  templateUrl: './gestion-admin-companies.component.html',
  styleUrl: './gestion-admin-companies.component.css'
})
export class GestionAdminCompaniesComponent implements OnInit, AfterViewInit {

  displayedColumns: Array<keyof Company | string> = [
    'raisonSociale',
    'financialSupport',
    'stage',
    'revenue',
    'siretNumber',
    'email',
    'phoneNumber'
  ];

  dataSource = new MatTableDataSource<Company>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private companyService: CompanyService) {}

  ngOnInit(): void {
    // Récupère les données et les pousse dans la table
    this.companyService.getAll().subscribe((companies) => {
      this.dataSource.data = companies ?? [];
    });

    // Filtre par défaut : insensible à la casse et cherche dans toutes les colonnes affichées
    this.dataSource.filterPredicate = (data: Company, filter: string) => {
      const f = filter.trim().toLowerCase();
      return [
        data.raisonSociale,
        data.stage,
        data.revenue,
        data.siretNumber,
        data.email,
        data.phoneNumber,
        data.financialSupport ? 'oui' : 'non'
      ]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()
        .includes(f);
    };
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(value: string): void {
    this.dataSource.filter = value.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
