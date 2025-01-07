import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DesignerDetailsComponent } from './designer-details.component';
import { TranslateModule } from '@ngx-translate/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { DesignerService } from '../services/designer.service';

describe('DesignerDetailsComponent', () => {
  let component: DesignerDetailsComponent;
  let fixture: ComponentFixture<DesignerDetailsComponent>;

  const mockDesignerService = {
    getDesignerById: jasmine.createSpy('getDesignerById').and.returnValue(
      of({
        id: '1',
        firstname: 'John',
        lastname: 'Doe',
        email: 'john.doe@example.com',
        profilePicture: '',
        biography: 'Test Biography',
        phoneNumber: '',
        profession: 'Designer',
        specialties: [],
        spheresOfInfluence: [],
        favoriteSectors: [],
        countryOfOrigin: '',
        countryOfResidence: '',
        professionalLevel: '',
        majorWorks: [],
        portfolioUrl: '',
      })
    ),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        DesignerDetailsComponent,
        HttpClientTestingModule,
        TranslateModule.forRoot(),
      ],
      providers: [
        { provide: DesignerService, useValue: mockDesignerService },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: jasmine.createSpy('get').and.returnValue('1'), // Mock de l'ID
              },
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DesignerDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getDesignerById with correct ID', () => {
    expect(mockDesignerService.getDesignerById).toHaveBeenCalledWith('1');
  });

  it('should set designer property after data is fetched', () => {
    expect(component.designer).toEqual({
      id: '1',
      firstname: 'John',
      lastname: 'Doe',
      email: 'john.doe@example.com',
      profilePicture: '',
      biography: 'Test Biography',
      phoneNumber: '',
      profession: 'Designer',
      specialties: [],
      spheresOfInfluence: [],
      favoriteSectors: [],
      countryOfOrigin: '',
      countryOfResidence: '',
      professionalLevel: '',
      majorWorks: [],
      portfolioUrl: ''
    });
  });
});
