import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../../shared/services/auth.service';
import { Subscription, switchMap } from 'rxjs';
import { Router } from '@angular/router';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { NotificationService } from '../../shared/services/notifcation.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { Location } from '@angular/common';
import { PasswordResetService } from '../../shared/services/password-reset.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    RouterModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSnackBarModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm: FormGroup;
  badCredentials = false;
  errorMessage = '';

  subscriptions = new Subscription();

  constructor(
    private readonly authService: AuthService,
    private readonly fb: FormBuilder,
    private readonly router: Router,
    private readonly notificationService: NotificationService,
    private readonly location: Location,
    private readonly passwordResetService: PasswordResetService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  // Message d'information quand l'utilisateur vient de creer son compte
  ngOnInit(): void {
    const state = this.location.getState() as { registrationSuccess?: boolean };

    if (state?.registrationSuccess) {
      this.notificationService.success(
        'Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter !'
      );
    }
  }

  /**
   * Log in de l'utilisateur
   */
  onSubmit() {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;

      const sub = this.authService.login(email, password).pipe(
        switchMap(() => this.authService.refreshSession())
      ).subscribe({
        next: () => {
          this.router.navigate(['account']);
        },
        error: (error) => {
          if(error.message.includes("401")) {
            this.badCredentials = true;
          } else {
            this.errorMessage = error.error.message;
          }
          
        }
      })
      this.subscriptions.add(sub);
    } else {
      this.errorMessage = "Le formulaire n'est pas rempli correctement.";
    }
  }

  /**
   * Procédure en cas d'oubli de mot de passe
   */
  onForgotPassword() {
    const email = prompt("Veuillez saisir votre adresse email pour réinitialiser votre mot de passe :");
    if (email) {
      this.passwordResetService.requestReset(email).subscribe({
        next: () => {
          this.notificationService.success("Un email de réinitialisation a été envoyé !");
        },
        error: () => {
          this.notificationService.error("Erreur lors de l'envoie de l'email. Veuillez réessayer.");
        }
      })
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
