import { Component, OnDestroy } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthService } from '../services/auth.service';
import { catchError, Observable, of, Subscription, switchMap } from 'rxjs';
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

  }

  ngOnInit() {
    // 1) On garde isLogged à jour
    this.subs.add(
      this.authService.$isLogged().subscribe(flag => this.isLogged = flag)
    );

    // 2) On crée un flux qui suit chaque changement de connexion
    this.designer$ = this.authService.$isLogged().pipe(
      switchMap(isLogged => {
        // Si connecté, on va chercher le designer; sinon on renvoie null
        if (isLogged && this.authService.getUserId()) {
          return this.designerService
            .getDesignerByUserId(this.authService.getUserId()!)
            .pipe(
              // en cas d’erreur (404), on transforme en null
              catchError(() => of(null))
            );
        }
        return of(null);
      })
    );
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
