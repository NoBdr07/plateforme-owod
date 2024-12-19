import { Component } from '@angular/core';
import { Designer } from '../interfaces/designer.interface';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { DesignerService } from '../services/designer.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-designer-details',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './designer-details.component.html',
  styleUrl: './designer-details.component.css',
})
export class DesignerDetailsComponent {
  designer!: Designer;

  constructor(
    private route: ActivatedRoute,
    private designerService: DesignerService
  ) {}

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
}
