import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-photo-dialog',
  standalone: true,
  imports: [
    MatFormFieldModule,
    CommonModule,
    MatInputModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    TranslateModule
  ],
  templateUrl: './photo-dialog.component.html',
  styleUrl: './photo-dialog.component.css',
})
export class PhotoDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<PhotoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { picture: File }
  ) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];

      // Vérification de la taille du fichier (max 2 Mo)
      const maxSizeMB = 3;
      if (file.size > maxSizeMB * 1024 * 1024) {
        alert(`Le fichier est trop volumineux. Taille maximale : ${maxSizeMB} Mo.`);
        return
      }

      this.data.picture = file;
    }
  }  

  onConfirm(): void {
    if (this.data.picture) {
      this.dialogRef.close(this.data.picture);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
