import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PhotoDialogComponent } from './photo-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';

describe('PhotoDialogComponent', () => {
  let component: PhotoDialogComponent;
  let fixture: ComponentFixture<PhotoDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PhotoDialogComponent, TranslateModule.forRoot()],
      providers: [
        {
          provide: MatDialogRef,
          useValue: { close: jasmine.createSpy('close') }, // Mock MatDialogRef
        },
        { provide: MAT_DIALOG_DATA, useValue: { photoUrl: 'https://example.com/photo.jpg', description: 'Test description' } },
       
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PhotoDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
