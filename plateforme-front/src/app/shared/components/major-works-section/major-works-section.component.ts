import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-major-works-section',
  standalone: true,
  imports: [CommonModule, TranslateModule, MatButtonModule],
  templateUrl: './major-works-section.component.html',
  styleUrl: './major-works-section.component.css'
})
export class MajorWorksSectionComponent {
  @Input() images: string[] = [];
  @Output() remove = new EventEmitter<string>();
  @Output() addFiles = new EventEmitter<FileList>();
  @Output() add = new EventEmitter<void>();

}
