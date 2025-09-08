import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule
  ],
  template: `
    <mat-toolbar color="primary" class="app-toolbar">
      <div class="toolbar-content">
        <div class="toolbar-left">
          <button mat-icon-button routerLink="/dashboard" class="logo-button">
            <mat-icon>local_hospital</mat-icon>
          </button>
          <span class="app-title" routerLink="/dashboard">CitaSmart</span>
        </div>

        <nav class="nav-links" *ngIf="authService.isAuthenticated()">
          <button mat-button routerLink="/dashboard" routerLinkActive="active-link">
            <mat-icon>dashboard</mat-icon>
            Dashboard
          </button>
          <button mat-button routerLink="/appointments" routerLinkActive="active-link">
            <mat-icon>event</mat-icon>
            Mis Citas
          </button>
          <button mat-button routerLink="/appointments/book" routerLinkActive="active-link">
            <mat-icon>add_circle</mat-icon>
            Nueva Cita
          </button>
        </nav>

        <div class="toolbar-right">
          <div *ngIf="authService.isAuthenticated(); else loginButtons" class="user-menu">
            <button mat-icon-button [matMenuTriggerFor]="userMenu">
              <mat-icon>account_circle</mat-icon>
            </button>
            <mat-menu #userMenu="matMenu">
              <button mat-menu-item routerLink="/profile">
                <mat-icon>person</mat-icon>
                Mi Perfil
              </button>
              <button mat-menu-item>
                <mat-icon>settings</mat-icon>
                Configuración
              </button>
              <mat-divider></mat-divider>
              <button mat-menu-item (click)="logout()">
                <mat-icon>logout</mat-icon>
                Cerrar Sesión
              </button>
            </mat-menu>
          </div>

          <ng-template #loginButtons>
            <button mat-button routerLink="/auth/login">
              Iniciar Sesión
            </button>
            <button mat-raised-button color="accent" routerLink="/auth/register">
              Registrarse
            </button>
          </ng-template>
        </div>
      </div>
    </mat-toolbar>
  `,
  styles: [`
    .app-toolbar {
      position: sticky;
      top: 0;
      z-index: 1000;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .toolbar-content {
      display: flex;
      align-items: center;
      width: 100%;
    }

    .toolbar-left {
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }

    .logo-button {
      color: white;
    }

    .app-title {
      font-size: 1.5rem;
      font-weight: 500;
      color: white;
      text-decoration: none;
      cursor: pointer;
    }

    .nav-links {
      display: flex;
      margin-left: 2rem;
      gap: 0.5rem;
    }

    .nav-links button {
      color: white;
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }

    .active-link {
      background-color: rgba(255, 255, 255, 0.1);
      border-radius: 4px;
    }

    .toolbar-right {
      margin-left: auto;
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .user-menu button {
      color: white;
    }

    @media (max-width: 768px) {
      .nav-links {
        display: none;
      }
      
      .app-title {
        font-size: 1.2rem;
      }
    }

    @media (max-width: 480px) {
      .toolbar-content {
        padding: 0 0.5rem;
      }
      
      .app-title {
        font-size: 1rem;
      }
    }
  `]
})
export class NavigationComponent {

  constructor(public authService: AuthService) { }

  logout() {
    this.authService.logout();
  }
}
