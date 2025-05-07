import { Component, OnDestroy } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { AuthService } from '../../shared/services/auth.service';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { RegisterRequest } from '../../shared/interfaces/register-request.interface';
import { Router, RouterModule } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox'
import { TranslateModule } from '@ngx-translate/core';
import { MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
    RouterModule,
    MatCheckboxModule,
    TranslateModule,
    MatSnackBarModule
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
      password: ['', [Validators.required, Validators.minLength(8)]],
      passwordConfirmation: ['', Validators.required],
      dataConsent: ['', Validators.requiredTrue]
    }, {validators: [this.emailMatchValidator(), this.passwordMatchValidator()]});
  }

  /**
   * Validator pour controler que les deux adresses mail sont les memes
   * @returns 
   */
  emailMatchValidator(): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const email = formGroup.get('email')?.value;
      const confirmedEmail = formGroup.get('emailConfirmation')?.value;
  
      if (email && confirmedEmail && email !== confirmedEmail) {
        formGroup.get('emailConfirmation')?.setErrors({ emailsDoNotMatch: true });
        return { emailsDoNotMatch: true };
      } else {
        formGroup.get('emailConfirmation')?.setErrors(null);
        return null;
      }
    };
  }

  /**
   * Validator pour controler que les deux mots de passe sont les memes
   * @returns 
   */
  passwordMatchValidator(): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const password = formGroup.get('password')?.value;
      const confirmedPassword = formGroup.get('passwordConfirmation')?.value;
  
      if (password && confirmedPassword && password !== confirmedPassword) {
        formGroup.get('passwordConfirmation')?.setErrors({ passwordsDoNotMatch: true });
        return { passwordsDoNotMatch: true };
      } else {
        formGroup.get('passwordConfirmation')?.setErrors(null);
        return null;
      }
    };
  }

  /**
   * Soumission du formulaire
   */
  onSubmit() {
    if(this.registerForm.valid) {
      const registerRequest = this.registerForm.value as RegisterRequest;
      registerRequest.admin = false;

      const sub = this.authService.register(registerRequest).subscribe({
        next: () => {          
          this.router.navigate(['login'], {
            state: { registrationSuccess: true }
          });
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
