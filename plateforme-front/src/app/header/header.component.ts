import { Component, OnDestroy } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthService } from '../services/auth.service';
import { Observable, Subscriber, Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { DesignerService } from '../services/designer.service';
import { Designer } from '../interfaces/designer.interface';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule, TranslateModule, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent implements OnDestroy {
  // selection d'anglais ou francais
  currentLang!: string;

  // menu ouvert ou non
  isMenuOpen = false;

  // utilisateur connecté ou non et ses infos
  isLogged = false;
  userId!: string | null;
  designer$!: Observable<Designer | null>;

  subs = new Subscription();

  constructor(
    private translateService: TranslateService,
    private authService: AuthService,
    private designerService: DesignerService
  ) {
    // Initialiser avec la langue courante
    this.currentLang =
      this.translateService.currentLang ||
      this.translateService.getDefaultLang();

    // Menu en fonction de si la personne est connectée ou non
    const sub = this.authService.$isLogged().subscribe((isLogged) => {
      this.isLogged = isLogged;

      if (isLogged) {
        this.userId = this.authService.getUserId();

        if (this.userId) {
          this.designer$ = this.designerService.getDesignerByUserId(
            this.userId
          );
        } else {
          this.designer$ = new Observable<null>((subscriber) =>
            subscriber.next(null)
          );
        }
      } else {
        this.userId = null;
        this.designer$ = new Observable<null>((subscriber) =>
          subscriber.next(null)
        );
      }
    });

    this.subs.add(sub);
  }

  /**
   * Passage en anglais ou francais
   * @param lang 
   */
  switchLanguage(lang: string) {
    this.currentLang = lang;
    this.translateService.use(lang);
  }

  /**
   * Ouverture et fermeture du menu
   */
  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
    const burgerBtn = document.querySelector('.burger-menu');
    burgerBtn?.classList.toggle('active');
  }

  /**
   * Unsubscribe des observables
   */
  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
