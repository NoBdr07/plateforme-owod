import {
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  Renderer2,
} from '@angular/core';
import { DesignerService } from '../../shared/services/designer.service';
import { Designer } from '../../shared/interfaces/designer.interface';
import { Observable, Subscription, tap } from 'rxjs';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, MatButtonModule, RouterModule, TranslateModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit, OnDestroy {
  designers$!: Observable<Designer[]>;
  totalDesigners = 0;
  subs = new Subscription();
  private observer!: IntersectionObserver;

  constructor(
    private readonly designerService: DesignerService,
    private readonly el: ElementRef,
    private readonly renderer: Renderer2
  ) {}

  ngOnInit(): void {
    // Chargement des données de designer pour la partie "tant de designer nous ont déja rejoints"
    const sub = this.designerService.loadDesigners().subscribe();
    this.designers$ = this.designerService.getDesigners().pipe(
      tap((designers) => this.totalDesigners = designers.length)
    );

    this.setupIntersectionObserver();

    this.subs.add(sub);
  }

  // observer des elements qui sont animés quand il arrive a l'écran
  private setupIntersectionObserver(): void {
    this.observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            const features = (entry.target as HTMLElement).querySelectorAll(
              '.feature'
            );
            const infos = (entry.target as HTMLElement).querySelectorAll(
              '.info'
            );

            features.forEach((features, index) => {
              if (index === 1) {
                this.renderer.addClass(features, 'fade-in-bottom');
              } else {
                this.renderer.addClass(features, 'fade-in-top');
              }
            });

            infos.forEach((infos, index) => {
              this.renderer.addClass(infos, 'fade-in-left');
            });

            this.observer.unobserve(entry.target);
          }
        });
      },
      {
        threshold: 0.4,
      }
    );

    // Commencer l'observation du container features
    const featuresContainer = this.el.nativeElement.querySelector(
      '.features-container'
    );
    if (featuresContainer) {
      this.observer.observe(featuresContainer);
    }

    // Commencer l'observation du container features
    const infoContainer =
      this.el.nativeElement.querySelector('.info-container');
    if (infoContainer) {
      this.observer.observe(infoContainer);
    }
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();

    if (this.observer) {
      this.observer.disconnect();
    }
  }
}
