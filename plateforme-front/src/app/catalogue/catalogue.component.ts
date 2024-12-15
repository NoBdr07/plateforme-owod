import { Component, OnDestroy, OnInit } from '@angular/core';
import { Designer } from '../interfaces/designer.interface';
import { Observable, Subscription } from 'rxjs';
import { DesignerService } from '../services/designer.service';
import { CommonModule } from '@angular/common';
import { MatMenuModule } from '@angular/material/menu'
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-catalogue',
  standalone: true,
  imports: [CommonModule, MatMenuModule, MatButtonModule, RouterModule],
  templateUrl: './catalogue.component.html',
  styleUrl: './catalogue.component.css'
})
export class CatalogueComponent implements OnInit, OnDestroy {

  designers$!: Observable<Designer[]>;
  subs = new Subscription();

  constructor(private readonly designerService: DesignerService) {}

  ngOnInit(): void {
      this.subs.add(this.designerService.loadDesigners().subscribe());
      this.designers$ = this.designerService.getDesigners();
  }

  ngOnDestroy(): void {
      this.subs.unsubscribe();
  }
}
