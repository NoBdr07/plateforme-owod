import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule, TranslateModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  currentLang!: string;
  isMenuOpen = false;

  constructor(private translateService: TranslateService) {
    // Initialiser avec la langue courante
    this.currentLang = this.translateService.currentLang || this.translateService.getDefaultLang();
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

}
