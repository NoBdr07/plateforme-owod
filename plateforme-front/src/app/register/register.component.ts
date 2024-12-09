import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage = '';

  constructor(
    private readonly http: HttpClient,
    private readonly authService: AuthService,
    private readonly fb: FormBuilder
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      emailConfirmation: ['', [Validators.required, Validators.email]],
      username: ['', [Validators.required]],
      password: ['', Validators.required]
    }, {validators: this.emailMatchValidator()});
  }

  emailMatchValidator(): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      const email = formGroup.get('email')?.value;
      const confirmedEmail = formGroup.get('confirmedEmail')?.value;
  
      // Si les emails ne correspondent pas, retourne une erreur
      return email && confirmedEmail && email !== confirmedEmail ? { emailsDoNotMatch: true } : null;
    };
  }

  onSubmit() {
    
  }
}
