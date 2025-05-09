import { CommonModule } from '@angular/common';
import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
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
  @Output() edit = new EventEmitter<void>();

  dialog = inject(MatDialog);
  designerService = inject(DesignerService);

}
