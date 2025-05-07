import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionCalendrierComponent } from './gestion-calendrier.component';

describe('GestionCalendrierComponent', () => {
  let component: GestionCalendrierComponent;
  let fixture: ComponentFixture<GestionCalendrierComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestionCalendrierComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GestionCalendrierComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
