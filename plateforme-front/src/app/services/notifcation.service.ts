import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  constructor(private snackBar: MatSnackBar) {}

  /**
   * Ouvre une fenêtre de notification avec un message de succès
   * @param message 
   */
  success(message: string) {
    console.log("appel de notificaion service");
    this.snackBar.open(message, 'Fermer', {
      duration: 10000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['success-snackbar']
    });
  }

  /**
   * Ouvre une fenêtre avec un message d'erreur
   * @param message 
   */
  error(message: string) {
    this.snackBar.open(message, 'Fermer', {
      duration: 10000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['error-snackbar']
    });
  }
}