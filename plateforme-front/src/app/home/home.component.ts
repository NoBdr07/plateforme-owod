import { Component, OnDestroy, OnInit } from '@angular/core';
import { DesignerService } from '../services/designer.service';
import { Designer } from '../interfaces/designer.interface';
import { Observable, Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit, OnDestroy {

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
