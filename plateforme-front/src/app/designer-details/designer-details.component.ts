import { Component } from '@angular/core';
import { Designer } from '../interfaces/designer.interface';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { DesignerService } from '../services/designer.service';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-designer-details',
  standalone: true,
  imports: [CommonModule, RouterModule, TranslateModule, MatButtonModule],
  templateUrl: './designer-details.component.html',
  styleUrl: './designer-details.component.css',
})
export class DesignerDetailsComponent {
  designer!: Designer;
  previewImage: string | null = null; 
  isLogged$: Observable<boolean>;

  constructor(
    private route: ActivatedRoute,
    private designerService: DesignerService,
    private authService: AuthService
  ) {
    this.isLogged$ = this.authService.$isLogged();
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id'); // Récupère l'ID depuis l'URL
    if (id) {
      this.designerService.getDesignerById(id).subscribe((data) => {
        if (data) {
          this.designer = data;
        }
      });
    }
  }

  /**
   * Ouverture du grossissement d'une réalisation
   * @param imageUrl 
   */
  showPreview(imageUrl: string): void {
    this.previewImage = imageUrl; 
  }

  /**
   * Fermeture du grossissement
   */
  hidePreview(): void {
    this.previewImage = null; 
  }

}
