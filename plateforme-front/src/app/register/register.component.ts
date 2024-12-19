import { Component, OnDestroy } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { RegisterRequest } from '../interfaces/register-request.interface';
import { Router, RouterModule } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
    RouterModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnDestroy {
  registerForm: FormGroup;
  errorMessage = '';

  subscriptions = new Subscription;

  constructor(
    private readonly authService: AuthService,
    private readonly fb: FormBuilder,
    private readonly router: Router
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      emailConfirmation: ['', [Validators.required, Validators.email]],
      firstname: ['', [Validators.required]],
      lastname: ['', [Validators.required]],
      password: ['', Validators.required],
      passwordConfirmation: ['', Validators.required],
    }, {validators: [this.emailMatchValidator(), this.passwordMatchValidator()]});
  }

  emailMatchValidator(): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const email = formGroup.get('email')?.value;
      const confirmedEmail = formGroup.get('emailConfirmation')?.value;
  
      // Si les emails ne correspondent pas, retourne une erreur
      return email && confirmedEmail && email !== confirmedEmail ? { emailsDoNotMatch: true } : null;
    };
  }

  passwordMatchValidator(): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const password = formGroup.get('password')?.value;
      const confirmedPassword = formGroup.get('passwordConfirmation')?.value;
  
      // Si les emails ne correspondent pas, retourne une erreur
      return password && confirmedPassword && password !== confirmedPassword ? { emailsDoNotMatch: true } : null;
    };
  }

  onSubmit() {
    if(this.registerForm.valid) {
      const registerRequest = this.registerForm.value as RegisterRequest;
      registerRequest.admin= false;

      const sub = this.authService.register(registerRequest).subscribe({
        next: () => {
          this.router.navigate(['login']);
        },
        error: (error) => {
          this.errorMessage = error.message;
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
