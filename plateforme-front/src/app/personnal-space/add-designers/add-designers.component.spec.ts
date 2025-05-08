import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddDesignersComponent } from './add-designers.component';

describe('AddDesignersComponent', () => {
  let component: AddDesignersComponent;
  let fixture: ComponentFixture<AddDesignersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddDesignersComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AddDesignersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
