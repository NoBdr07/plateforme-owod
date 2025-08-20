import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompanyFormSectionComponent } from './company-form-section.component';

describe('CompanyFormSectionComponent', () => {
  let component: CompanyFormSectionComponent;
  let fixture: ComponentFixture<CompanyFormSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompanyFormSectionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CompanyFormSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
