import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { NotificationService } from '../services/notifcation.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { Location } from '@angular/common';

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
  errorMessage = '';

  subscriptions = new Subscription();

  constructor(
    private readonly authService: AuthService,
    private readonly fb: FormBuilder,
    private readonly router: Router,
    private readonly notificationService: NotificationService,
    private readonly location: Location
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    const state = this.location.getState() as { registrationSuccess?: boolean };

    if (state.registrationSuccess) {
      console.log('Notification déclenchée dans le ngOnInit');
      this.notificationService.success(
        'Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter !'
      );
    }
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;

      const sub = this.authService.login(email, password).subscribe({
        next: () => {
          this.authService.checkTokenPresence();
          this.router.navigate(['account']);
        },
        error: (error) => {
          this.errorMessage = error.error.message;
        },
      });
      this.subscriptions.add(sub);
    } else {
      this.errorMessage = "Le formulaire n'est pas rempli correctement.";
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
