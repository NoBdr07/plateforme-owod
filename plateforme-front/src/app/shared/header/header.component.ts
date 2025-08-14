import { Component, OnDestroy } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthService } from '../services/auth.service';
import { catchError, map, Observable, of, shareReplay, Subscription, switchMap } from 'rxjs';
import { CommonModule } from '@angular/common';
import { DesignerService } from '../services/designer.service';
import { Designer } from '../interfaces/designer.interface';
import { AccountType } from '../enums/account-type.enum';
import { CompanyService } from '../services/company.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule, TranslateModule, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent {
  // selection d'anglais ou francais
  currentLang!: string;

  // menu ouvert ou non
  isMenuOpen = false;

  // utilisateur connecté ou non et ses infos
  session$ = this.authService.session$;

  // avatar dérivé de la session
  avatar$ = this.session$.pipe(
    switchMap(s => {
      if (!s.isLogged || !s.userId ) return of(null);

      if (s.accountType === AccountType.DESIGNER) {
        return this.designerService.getDesignerByUserId(s.userId).pipe(
          map(d => d?.profilePicture ?? null),
          catchError(() => of(null))
        )
      }

      if (s.accountType === AccountType.COMPANY) {
        return this.companyService.getCompanyByUserId(s.userId).pipe(
          map(c => c?.logoUrl ?? null),
          catchError(() => of(null))
        )
      }

      return of(null);
    }),
    shareReplay(1)
  )

  subs = new Subscription();

  constructor(
    private translateService: TranslateService,
    private authService: AuthService,
    private designerService: DesignerService,
    private companyService: CompanyService
  ) {
    // Initialiser avec la langue courante
    this.currentLang =
      this.translateService.currentLang ||
      this.translateService.getDefaultLang();

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

}
