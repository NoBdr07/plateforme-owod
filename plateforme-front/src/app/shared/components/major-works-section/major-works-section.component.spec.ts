import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MajorWorksSectionComponent } from './major-works-section.component';

describe('MajorWorksSectionComponent', () => {
  let component: MajorWorksSectionComponent;
  let fixture: ComponentFixture<MajorWorksSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MajorWorksSectionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MajorWorksSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
