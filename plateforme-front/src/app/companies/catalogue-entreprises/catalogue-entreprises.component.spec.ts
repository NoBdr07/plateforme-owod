import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CatalogueEntreprisesComponent } from './catalogue-entreprises.component';

describe('CatalogueEntreprisesComponent', () => {
  let component: CatalogueEntreprisesComponent;
  let fixture: ComponentFixture<CatalogueEntreprisesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CatalogueEntreprisesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CatalogueEntreprisesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
