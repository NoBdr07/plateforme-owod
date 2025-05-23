import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransferDialogComponent } from './transfer-dialog.component';

describe('TransferDialogComponent', () => {
  let component: TransferDialogComponent;
  let fixture: ComponentFixture<TransferDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TransferDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
