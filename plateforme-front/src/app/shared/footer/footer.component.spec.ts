import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FooterComponent } from './footer.component';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('FooterComponent', () => {
  let component: FooterComponent;
  let fixture: ComponentFixture<FooterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FooterComponent, TranslateModule.forRoot(), HttpClientTestingModule ],
      providers: [
        TranslateService,
        {
                  provide: ActivatedRoute,
                  useValue: {
                    snapshot: {
                      params: {},
                      queryParams: {},
                    },
                  },
                },
      ]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
