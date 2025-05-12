import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,   
} from '@angular/material/dialog';
import { UserService } from '../../shared/services/user.service';
import { User } from '../../shared/interfaces/user.interface';
import { catchError, of } from 'rxjs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-transfer-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './transfer-dialog.component.html',
  styleUrl: './transfer-dialog.component.css',
})
export class TransferDialogComponent {
  form: FormGroup;
  loading = false;
  lookupError: string | null = null;
  targetUser: User | null = null;

  constructor(
    private userService: UserService,
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<TransferDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { designerId: string }
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
    });
  }

  onLookup() {
    this.lookupError = null;
    this.targetUser = null;
    this.loading = true;

    const email = this.form.value.email;

    this.userService
      .adminGetUserByEmail(email)
      .pipe(
        catchError(() => {
          this.lookupError = 'Utilisateur introuvable';
          return of(null);
        })
      )
      .subscribe((user) => {
        this.loading = false;
        if (!user) return;
        if (user.designerId) {
          this.lookupError = 'Cet utilisateur a déjà un designer';
        } else {
          this.targetUser = user;
        }
      });
  }

  onConfirm() {
    this.dialogRef.close({ userId: this.targetUser?.userId });
  }
}
