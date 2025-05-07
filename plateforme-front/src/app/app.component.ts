import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { HeaderComponent } from './shared/header/header.component';
import { TranslateModule } from '@ngx-translate/core';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { FooterComponent } from './shared/footer/footer.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet, 
    HeaderComponent,
    TranslateModule,
    MatSnackBarModule,
    FooterComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'plateforme-owod';

  constructor(private router: Router) {}

  ngOnInit(): void {
      this.router.events.subscribe(event => {
        if (event instanceof NavigationEnd) {
          window.scrollTo(0, 0);
        }
      })
  }
}
