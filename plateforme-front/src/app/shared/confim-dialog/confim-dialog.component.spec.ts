import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfimDialogComponent } from './confim-dialog.component';

describe('ConfimDialogComponent', () => {
  let component: ConfimDialogComponent;
  let fixture: ComponentFixture<ConfimDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfimDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ConfimDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
