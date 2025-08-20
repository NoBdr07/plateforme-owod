import { CommonModule } from '@angular/common';
import { Component, ContentChild, ElementRef, EventEmitter, inject, Input, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { DesignerService } from '../../services/designer.service';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-photo-section',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatDialogModule, TranslateModule],
  templateUrl: './photo-section.component.html',
  styleUrl: './photo-section.component.css'
})
export class PhotoSectionComponent {
  @Input() photoUrl!: string;
  @Input() altText = 'Photo'; 
  @Input() maxSizeMB = 3; // valeur par défaut
  @Input() defaultLabelKey = 'DASHBOARD.CHANGE_PHOTO';
  @Input() backgroundSrc = 'assets/images/back.png';
  @Input() accept = 'image/*';

  @Output() fileSelected = new EventEmitter<File>();

  errorMsg: string | null = null;

  dialog = inject(MatDialog);
  designerService = inject(DesignerService);

  /** Vrai si le parent projette un label personnalisé */
  @ContentChild('projectedLabel', { read: ElementRef }) projectedLabelRef?: ElementRef;
  get hasProjectedLabel(): boolean {
    return !!this.projectedLabelRef;
  }

  onFileSelected(event: Event): void {
    this.errorMsg = null;
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];

    // Vérification de la taille max
    const maxBytes = this.maxSizeMB * 1024 * 1024;
    if (file.size > maxBytes) {
      this.errorMsg = `Le fichier est trop volumineux. Taille maximale : ${this.maxSizeMB} Mo.`;
      // Permettre de re-sélectionner le même fichier
      input.value = '';
      return;
    }

    // OK → on propage au parent
    this.fileSelected.emit(file);

    // Autoriser re-sélection du même fichier
    input.value = '';
  }

}
