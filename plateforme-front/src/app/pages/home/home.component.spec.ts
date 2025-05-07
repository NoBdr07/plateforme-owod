import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HomeComponent } from './home.component';
import { DesignerService } from '../../shared/services/designer.service';
import { WeeklyDesignerService } from '../../shared/services/weekly-designer.service';
import { of } from 'rxjs';
import { TranslateModule } from '@ngx-translate/core';
import { Designer } from '../../shared/interfaces/designer.interface';
import { ActivatedRoute } from '@angular/router';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let mockDesignerService: jasmine.SpyObj<DesignerService>;
  let mockWeeklyDesignerService: jasmine.SpyObj<WeeklyDesignerService>;

  const mockDesigner: Designer = {
    id: '1',
    email: 'designer@example.com',
    profilePicture: 'https://example.com/profile.jpg',
    firstname: 'John',
    lastname: 'Doe',
    biography: 'An experienced designer specializing in UI/UX.',
    phoneNumber: '+1234567890',
    profession: 'UI/UX Designer',
    specialties: ['UI Design', 'UX Research'],
    spheresOfInfluence: ['Technology', 'Health'],
    favoriteSectors: ['Education', 'E-commerce'],
    countryOfOrigin: 'USA',
    countryOfResidence: 'Canada',
    professionalLevel: 'senior',
    majorWorks: [
      'https://example.com/work1.jpg',
      'https://example.com/work2.jpg',
    ],
    portfolioUrl: 'https://portfolio.example.com',
  };

  beforeEach(async () => {
    // Mock des services
    mockDesignerService = jasmine.createSpyObj('DesignerService', ['loadDesigners', 'getDesigners']);
    mockWeeklyDesignerService = jasmine.createSpyObj('WeeklyDesignerService', ['weeklyDesigner$']);

    // Configurer les valeurs de retour des méthodes mockées
    mockDesignerService.loadDesigners.and.returnValue(of());
    mockDesignerService.getDesigners.and.returnValue(of([]));
    mockWeeklyDesignerService.weeklyDesigner$ = of(mockDesigner);

    await TestBed.configureTestingModule({
      imports: [HomeComponent, HttpClientTestingModule, TranslateModule.forRoot()],
      providers: [
        { provide: DesignerService, useValue: mockDesignerService },
        { provide: WeeklyDesignerService, useValue: mockWeeklyDesignerService },
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
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call loadDesigners on init', () => {
    expect(mockDesignerService.loadDesigners).toHaveBeenCalled();
  });

  it('should assign designers$ with the observable from getDesigners', () => {
    component.ngOnInit();
    expect(mockDesignerService.getDesigners).toHaveBeenCalled();
    component.designers$.subscribe(designers => {
      expect(designers).toEqual([]);
    });
  });

  it('should unsubscribe on destroy', () => {
    const unsubscribeSpy = spyOn(component['subs'], 'unsubscribe');
    component.ngOnDestroy();
    expect(unsubscribeSpy).toHaveBeenCalled();
  });
});