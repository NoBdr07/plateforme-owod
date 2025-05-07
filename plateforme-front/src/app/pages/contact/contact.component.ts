import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ContactService } from '../../shared/services/contact.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateModule } from '@ngx-translate/core';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ContactData } from '../../shared/interfaces/contact-data.interface';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    TranslateModule,
    MatOptionModule,
    MatSelectModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.css'
})
export class ContactComponent {
  contactForm: FormGroup;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private contactService: ContactService,
    private snackBar: MatSnackBar
  ) {
    this.contactForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      subject: ['', Validators.required],
      reason: ['', Validators.required],
      description: ['', Validators.required],
    });
  }

  /**
   * envoi de la requête pour l'envoi du mail suite à la soumision du formulaire de contact
   */
  onSubmit(): void {
    if (this.contactForm.valid) {
      this.isLoading = true;
      this.contactService.sendContactEmail(this.contactForm.value as ContactData).subscribe({
        next: () => {
          this.snackBar.open('Message envoyé avec succès.', 'Fermer', { duration: 3000 });
          this.isLoading = false;
          
          // Réinitialisation du formulaire avec des valeurs initiales
        this.contactForm.reset({
          email: '',
          subject: '',
          reason: '',
          description: '',
        });

        // Remettre tous les champs à "pristine" et "untouched"
        Object.keys(this.contactForm.controls).forEach(key => {
          const control = this.contactForm.get(key);
          control?.setErrors(null); // Supprimer toutes les erreurs
          control?.markAsPristine();
          control?.markAsUntouched();
        });
        
        },
        error: () => {
          this.snackBar.open('Erreur lors de l’envoi du message.', 'Fermer', { duration: 3000 });
          this.isLoading = false;
        },
      });
    }
  }
}
