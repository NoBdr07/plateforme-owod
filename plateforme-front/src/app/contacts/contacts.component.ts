import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { UserService } from '../services/user.service';
import { Designer } from '../interfaces/designer.interface';
import { MatCardModule } from '@angular/material/card'
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-contacts',
  standalone: true,
  imports: [CommonModule, TranslateModule, RouterModule, MatCardModule, MatButtonModule],
  templateUrl: './contacts.component.html',
  styleUrl: './contacts.component.css',
})
export class ContactsComponent implements OnInit, OnDestroy {
  friends: Designer[] = [];
  userId: String | null = '';
  isLoading = false;
  error: String | null = null;

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadFriends();
  }

  loadFriends(): void {
    this.isLoading = true ;

    this.userService.getUserFriends().subscribe({
      next: (designers) => {
        this.friends = designers;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = "Erreur au chargement des amis";
        console.log("erreur au chargement des amis : " + err);
        this.isLoading = false;
      }
    })
  }

  redirect(friendId: string): void {
    this.router.navigate(['/details', friendId]);
  }

  deleteFriend(friendId: string): void {
    this.userService.deleteFriend(friendId).subscribe({
      next: () => {
        this.loadFriends();
      },
      error : () => {
        this.error = "Erreur lors de la suppression du contact";
      }
    })
  }

  ngOnDestroy(): void {}
}
