import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DesignerFormSectionComponent } from './designer-form-section.component';

describe('DesignerFormSectionComponent', () => {
  let component: DesignerFormSectionComponent;
  let fixture: ComponentFixture<DesignerFormSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DesignerFormSectionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DesignerFormSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
