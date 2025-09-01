import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionAdminCompaniesComponent } from './gestion-admin-companies.component';

describe('GestionAdminCompaniesComponent', () => {
  let component: GestionAdminCompaniesComponent;
  let fixture: ComponentFixture<GestionAdminCompaniesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestionAdminCompaniesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GestionAdminCompaniesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
