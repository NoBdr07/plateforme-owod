import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { UserService } from '../services/user.service';
import { Designer } from '../interfaces/designer.interface';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-contacts',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
  ],
  templateUrl: './contacts.component.html',
  styleUrl: './contacts.component.css',
})
export class ContactsComponent implements OnInit, OnDestroy {
  friends: Designer[] = [];
  userId: String | null = '';
  isLoading = false;
  error: String | null = null;

  private subs = new Subscription();

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit(): void {
    this.loadFriends();
  }

  /**
   * Chargement des contacts de l'utilisateur
   */
  loadFriends(): void {
    this.isLoading = true;

    const sub = this.userService.getUserFriends().subscribe({
      next: (designers) => {
        this.friends = designers;
        this.isLoading = false;
      },
      error: (err) => {
        if (err.error === 'No designer account associated with this user') {
          this.error =
            'Vous devez créer votre profil designer pour ajouter des contacts !';
        } else {
          this.error = 'Erreur au chargement des amis';
          console.log('erreur au chargement des amis : ' + err.error);
          this.isLoading = false;
        }
      },
    });

    this.subs.add(sub);
  }

  /** 
   * Pour se rendre directement sur la page du designer
   */
  redirect(friendId: string): void {
    this.router.navigate(['/details', friendId]);
  }


   /**
    * Suppression d'un contact et rechargement des contacts à l'issue
    * @param friendId 
    */
  deleteFriend(friendId: string): void {
    const sub = this.userService.deleteFriend(friendId).subscribe({
      next: () => {
        this.loadFriends();
      },
      error: () => {
        this.error = 'Erreur lors de la suppression du contact';
      },
    });

    this.subs.add(sub);
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
