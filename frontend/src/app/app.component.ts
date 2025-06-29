import { Component, HostListener, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink } from '@angular/router';
import {
  FontAwesomeModule,
  FaIconLibrary,
} from '@fortawesome/angular-fontawesome';
import {
  faUser,
  faChevronDown,
  faSignOutAlt,
} from '@fortawesome/free-solid-svg-icons';
import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, FontAwesomeModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  public auth = inject(AuthService);
  userEmail$ = this.auth.userEmail$;
  menuOpen = false;

  constructor(library: FaIconLibrary) {
    library.addIcons(faUser, faChevronDown, faSignOutAlt);
  }

  toggleMenu(e: MouseEvent) {
    e.stopPropagation();
    this.menuOpen = !this.menuOpen;
  }

  logout() {
    this.auth.logout();
  }

  @HostListener('document:click')
  closeMenu() {
    this.menuOpen = false;
  }
}
