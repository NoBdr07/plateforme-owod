import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PasswordResetService } from '../services/password-reset.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
  ],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css',
})
export class ResetPasswordComponent {
  resetForm = this.fb.group({
    password: ['', Validators.required],
  });

  constructor(
    private readonly route: ActivatedRoute,
    private readonly fb: FormBuilder,
    private readonly router: Router,
    private readonly passwordResetService: PasswordResetService
  ) {}

  onSubmit() {
    if (this.resetForm.valid) {
      const token = this.route.snapshot.queryParams['token'];
      this.passwordResetService
        .resetPassword(token, this.resetForm.value.password!)
        .subscribe(() => {
          this.router.navigate(['/login']);
        });
    }
  }
}
