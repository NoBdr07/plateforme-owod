import {
  Component,
  OnInit,
  OnDestroy,
  AfterViewInit,
  ElementRef,
  ViewChildren,
  QueryList,
  HostListener,
  ViewChild,
  TemplateRef,
} from '@angular/core';
import { Designer } from '../interfaces/designer.interface';
import {
  BehaviorSubject,
  combineLatest,
  map,
  Observable,
  Subscription,
} from 'rxjs';
import { DesignerService } from '../services/designer.service';
import { CommonModule } from '@angular/common';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { MatTooltipModule, TooltipPosition } from '@angular/material/tooltip';
import { AuthService } from '../services/auth.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-catalogue',
  standalone: true,
  imports: [
    CommonModule,
    MatMenuModule,
    MatTooltipModule,
    MatButtonModule,
    RouterModule,
    TranslateModule,
    MatDialogModule,
  ],
  templateUrl: './catalogue.component.html',
  styleUrl: './catalogue.component.css',
})
export class CatalogueComponent implements OnInit, OnDestroy, AfterViewInit {
  designers$!: Observable<Designer[]>;

  // Utilisateur connecté ou non
  isLogged: boolean = false;

  // Sur téléphone ou ordinateur
  isMobile: boolean = false;

  // Variables pour les recherches
  researchDesigners$!: Observable<Designer[]>;
  private researchCriteria = new BehaviorSubject<{
    category: string;
    item: string;
  } | null>(null);

  // Listes des specialités, pays, spheres et secteurs présents pour les listes de recherche
  specialties!: string[];
  countries!: string[];
  spheres!: string[];
  sectors!: string[];

  // Variables pour la pagination
  paginatedDesigners$!: Observable<Designer[]>;
  currentPage = new BehaviorSubject<number>(1);
  maxPage = new BehaviorSubject<number>(1);
  pageSize: number = 50;

  // Variables pour l'affichage mobile (infos des designers qui s'affichent au scroll)
  @ViewChildren('designerCard') designerCards!: QueryList<ElementRef>;
  myDesignersInView: boolean[] = [];

  // Pour les bulles d'infos
  tooltipPosition: TooltipPosition = 'above';

  // Pour popup numero et mail
  phone: string = '';
  email: string = '';
  @ViewChild('phoneDialogTemplate') phoneDialogTemplate!: TemplateRef<any>;
  @ViewChild('emailDialogTemplate') emailDialogTemplate!: TemplateRef<any>;

  // Contacts de l'utilisateur connecté pour adapter les logos d'ajout d'ami
  friends: Designer[] = [];

  subs = new Subscription();

  constructor(
    private readonly designerService: DesignerService,
    private readonly authService: AuthService,
    private readonly userService: UserService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  /**
   * Initialisation de la liste des designers, des listes servant aux recherches parmi les designers
   */
  ngOnInit(): void {
    this.checkIfMobile();
    window.addEventListener('resize', () => {
      this.checkIfMobile();
    });

    this.subs.add(
      this.designerService.loadDesigners().subscribe((designers) => {
        this.specialties = this.getUniqueValues(designers, 'specialties');
        this.spheres = this.getUniqueValues(designers, 'spheresOfInfluence');
        this.sectors = this.getUniqueValues(designers, 'favoriteSectors');
        this.countries = this.getUniqueValues(designers, 'countryOfResidence');
      })
    );

    this.designers$ = this.designerService.getDesigners();

    this.researchDesigners$ = combineLatest([
      this.designers$,
      this.researchCriteria,
    ]).pipe(
      map(([designers, criteria]) => {
        if (!criteria) return designers; // Retourne tous les designers si aucun filtre
        const { category, item } = criteria;

        // liste pour l'affichage des designers sur mobile, initialisée à false pour tous les designers
        this.myDesignersInView = new Array(designers.length).fill(false);

        // si un critère est présent, filtre des designers selon le critère
        return designers.filter((designer) => {
          if (category === 'profession') return designer.profession === item;
          if (category === 'specialty')
            return designer.specialties.includes(item);
          if (category === 'sphere')
            return designer.spheresOfInfluence.includes(item);
          if (category === 'sector')
            return designer.favoriteSectors.includes(item);
          if (category === 'country')
            return designer.countryOfResidence.trim() === item.trim();
          return true;
        });
      })
    );

    // Calcul du nombre max de page
    this.subs.add(
      this.researchDesigners$.subscribe((filteredDesigners) => {
        const total = filteredDesigners.length;
        const maxPages = Math.ceil(total / this.pageSize);
        this.maxPage.next(maxPages);
      })
    );

    // Pagination
    this.paginatedDesigners$ = combineLatest([
      this.researchDesigners$,
      this.currentPage,
    ]).pipe(
      map(([designers, page]) => {
        const startIndex = (page - 1) * this.pageSize;
        const endIndex = startIndex + this.pageSize;

        return designers.slice(startIndex, endIndex);
      })
    );

    // Check si l'utilisateur est connecté ou non
    const sub = this.authService.$isLogged().subscribe((logged) => {
      this.isLogged = logged;
    });

    // Chargement des contact
    this.loadFriends();

    this.subs.add(sub);
  }

  /**
   * Une fois la page initialisée, écoute du scroll
   */
  ngAfterViewInit(): void {
    this.onWindowScroll();
  }

  // Check device
  checkIfMobile(): void {
    if (!this.isMobile) {
      this.isMobile = window.innerWidth <= 768;
    }
  }

  loadFriends(): void {
    const sub = this.userService.getUserFriends().subscribe({
      next: (designers) => {
        this.friends = designers;
      },
      error: (err) => {
        console.log("erreur au chargement des amis : " + err);
      }
    })

    this.subs.add(sub);
  }

  isFriend(designerId: string): boolean {
    return this.friends.some(friend => friend.id === designerId);
  }

  /**
   * Gestion du scroll pour l'affichage dynamique des infos des
   * designers sur mobile
   */
  @HostListener('window:scroll', ['$event'])
  onWindowScroll() {
    if (window.innerWidth >= 768) {
      return;
    }

    const centerY = window.innerHeight / 2;

    // On vérifie la position de chaque designer card pour lui accorder true si il est
    // au milieu de l'écran
    this.designerCards.forEach((card, i) => {
      const rect = card.nativeElement.getBoundingClientRect();
      if (rect.top < centerY && rect.bottom > centerY) {
        this.myDesignersInView[i] = true;
      } else {
        this.myDesignersInView[i] = false;
      }
    });
  }

  /**
   * Effectue une recherche dans la liste des designers
   * @param category (pays, secteurs, sphere d'influence, spécialité ou profession)
   * @param item (élément recherché dans la catégorie)
   */
  research(category: string, item: string): void {
    this.researchCriteria.next({ category, item });
    this.currentPage.next(1);
  }

  /**
   * Revenir a la liste de designers complète
   */
  resetResearch(): void {
    this.researchCriteria.next(null);
    this.currentPage.next(1);
  }

  /**
   * Passe à la pge suivante
   */
  nextPage(): void {
    this.currentPage.next(this.currentPage.value + 1);
  }

  /**
   * Passe à la page précédente
   */
  prevPage(): void {
    if (this.currentPage.value > 1) {
      this.currentPage.next(this.currentPage.value - 1);
    }
  }

  /**
   * Méthode pour extraire des valeurs uniques d'un champ spécifique
   */
  private getUniqueValues<T>(array: T[], key: keyof T): string[] {
    const values: string[] = array
      .map((item) => {
        const val = item[key];
        // Vérifie si val est un tableau, sinon convertit en tableau
        return Array.isArray(val)
          ? val.map((v) => (typeof v === 'string' ? v.trim() : v))
          : [typeof val === 'string' ? val.trim() : val];
      })
      .flat() as string[];

    return [...new Set(values)];
  }

  /**
   * Fenetre popup qui donne le numero si connecté
   * @param phone
   */
  showPhoneNumber(phone: string): void {
    if (this.isLogged) {
      this.phone = phone;
      this.dialog.open(this.phoneDialogTemplate);
    } else {
      if (this.isMobile) {
        this.snackBar.open(
          'Vous devez créer un compte pour accéder à cette fonctionnalité !',
          'Ok',
          {duration: 3000}
        );
      }
    }
  }

  /**
   * Fenetre popup qui donne l'email si connecté
   * @param email
   */
  showEmail(email: string): void {
    if (this.isLogged) {
      this.email = email;
      this.dialog.open(this.emailDialogTemplate);
    } else {
      if (this.isMobile) {
        this.snackBar.open(
          'Vous devez créer un compte pour accéder à cette fonctionnalité !',
          'Ok',
          {duration: 3000}
        );
      }
    }
  }

  addFriend(friendId: string): void {
    if (this.isLogged) {
      this.userService.addFriend(friendId).subscribe({
        next: () => {
          this.snackBar.open('Contact ajouté !', 'Ok', {duration: 3000});
          this.loadFriends();
        },
        error: () => {
          this.snackBar.open('Erreur lors de l\'ajout du contact', 'Ok', {duration: 3000});
        }
      })
    }
  }

  /**
   * Unsubscribe des observable
   */
  ngOnDestroy(): void {
    this.subs.unsubscribe();

    window.removeEventListener('resize', () => {
      this.checkIfMobile();
    });
  }
}
