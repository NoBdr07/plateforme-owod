import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './login.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TranslateModule } from '@ngx-translate/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { of } from 'rxjs';
import { FormBuilder } from '@angular/forms';
import { PasswordResetService } from '../../shared/services/password-reset.service';
import { AuthService } from '../../shared/services/auth.service';
import { NotificationService } from '../../shared/services/notifcation.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const mockLocation = {
    getState: () => ({ registrationSuccess: true}),
  }

  const mockNotificationService = {
    success: jasmine.createSpy('success'),
    error: jasmine.createSpy('error'),
  };

  const mockAuthService = {
    login: jasmine.createSpy('login').and.returnValue(of({})),
    checkTokenPresence: jasmine.createSpy('checkTokenPresence'),
  };

  const mockPasswordResetService = {
    requestReset: jasmine.createSpy('requestReset').and.returnValue(of({})),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent, HttpClientTestingModule, BrowserAnimationsModule, TranslateModule.forRoot(), RouterModule],
      providers: [
        { provide: Location, useValue: mockLocation },
        { provide: NotificationService, useValue: mockNotificationService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: PasswordResetService, useValue: mockPasswordResetService },
        { provide: FormBuilder, useClass: FormBuilder },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              params: {},
              queryParams: {},
            },
          },
        },
      ],
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
