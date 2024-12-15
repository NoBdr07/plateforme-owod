import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-photo-dialog',
  standalone: true,
  imports: [
    MatFormFieldModule,
    CommonModule,
    MatInputModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule
  ],
  templateUrl: './photo-dialog.component.html',
  styleUrl: './photo-dialog.component.css',
})
export class PhotoDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<PhotoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { url: string }
  ) {}

  onConfirm(): void {
    this.dialogRef.close(this.data.url);
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
