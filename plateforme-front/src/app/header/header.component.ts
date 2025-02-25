import { Component, OnDestroy } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthService } from '../services/auth.service';
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule, TranslateModule, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent implements OnDestroy {
  currentLang!: string;
  isMenuOpen = false;
  isLogged = false;

  subs = new Subscription();

  constructor(
    private translateService: TranslateService,
    private authService: AuthService
  ) {
    // Initialiser avec la langue courante
    this.currentLang =
      this.translateService.currentLang ||
      this.translateService.getDefaultLang();

    // Menu en fonction de si la personne est connectée ou non
    const sub = this.authService.$isLogged().subscribe(
      isLogged => this.isLogged = isLogged
    )

    this.subs.add(sub);
  }

  switchLanguage(lang: string) {
    this.currentLang = lang;
    this.translateService.use(lang);
  }

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
    const burgerBtn = document.querySelector('.burger-menu');
    burgerBtn?.classList.toggle('active');
  }

  ngOnDestroy(): void {
      this.subs.unsubscribe();
  }
}
