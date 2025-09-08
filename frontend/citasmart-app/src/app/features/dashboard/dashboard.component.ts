import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatGridListModule } from '@angular/material/grid-list';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatGridListModule
  ],
  template: `
    <div class="dashboard-container">
      <div class="dashboard-header">
        <h1>Panel de Control</h1>
        <p>Bienvenido a CitaSmart</p>
      </div>

      <mat-grid-list cols="3" rowHeight="200px" gutterSize="20px" class="dashboard-grid">
        <mat-grid-tile>
          <mat-card class="dashboard-card">
            <mat-card-header>
              <mat-icon mat-card-avatar>event</mat-icon>
              <mat-card-title>Mis Citas</mat-card-title>
              <mat-card-subtitle>Próximas citas</mat-card-subtitle>
            </mat-card-header>
            <mat-card-content>
              <div class="stat-number">5</div>
              <p>Citas programadas</p>
            </mat-card-content>
            <mat-card-actions>
              <button mat-button routerLink="/appointments">Ver todas</button>
            </mat-card-actions>
          </mat-card>
        </mat-grid-tile>

        <mat-grid-tile>
          <mat-card class="dashboard-card">
            <mat-card-header>
              <mat-icon mat-card-avatar>add_circle</mat-icon>
              <mat-card-title>Nueva Cita</mat-card-title>
              <mat-card-subtitle>Agendar cita</mat-card-subtitle>
            </mat-card-header>
            <mat-card-content>
              <p>Agenda una nueva cita de forma rápida y sencilla</p>
            </mat-card-content>
            <mat-card-actions>
              <button mat-raised-button color="primary" routerLink="/appointments/book">
                Agendar Cita
              </button>
            </mat-card-actions>
          </mat-card>
        </mat-grid-tile>

        <mat-grid-tile>
          <mat-card class="dashboard-card">
            <mat-card-header>
              <mat-icon mat-card-avatar>person</mat-icon>
              <mat-card-title>Mi Perfil</mat-card-title>
              <mat-card-subtitle>Información personal</mat-card-subtitle>
            </mat-card-header>
            <mat-card-content>
              <p>Actualiza tu información y preferencias</p>
            </mat-card-content>
            <mat-card-actions>
              <button mat-button routerLink="/profile">Ver perfil</button>
            </mat-card-actions>
          </mat-card>
        </mat-grid-tile>
      </mat-grid-list>

      <div class="recent-activity">
        <h2>Actividad Reciente</h2>
        <mat-card>
          <mat-card-content>
            <div class="activity-item">
              <mat-icon>event_available</mat-icon>
              <div class="activity-content">
                <h4>Cita confirmada</h4>
                <p>Tu cita para el 15 de enero ha sido confirmada</p>
                <span class="activity-time">Hace 2 horas</span>
              </div>
            </div>
            <div class="activity-item">
              <mat-icon>notifications</mat-icon>
              <div class="activity-content">
                <h4>Recordatorio</h4>
                <p>Tienes una cita mañana a las 10:00 AM</p>
                <span class="activity-time">Hace 1 día</span>
              </div>
            </div>
          </mat-card-content>
        </mat-card>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
    }

    .dashboard-header {
      text-align: center;
      margin-bottom: 2rem;
    }

    .dashboard-header h1 {
      margin: 0;
      color: #333;
    }

    .dashboard-grid {
      margin-bottom: 2rem;
    }

    .dashboard-card {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
    }

    .dashboard-card mat-card-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
    }

    .stat-number {
      font-size: 2rem;
      font-weight: bold;
      color: #673ab7;
      text-align: center;
    }

    .recent-activity h2 {
      margin-bottom: 1rem;
      color: #333;
    }

    .activity-item {
      display: flex;
      align-items: flex-start;
      gap: 1rem;
      padding: 1rem 0;
      border-bottom: 1px solid #eee;
    }

    .activity-item:last-child {
      border-bottom: none;
    }

    .activity-content {
      flex: 1;
    }

    .activity-content h4 {
      margin: 0 0 0.5rem 0;
      color: #333;
    }

    .activity-content p {
      margin: 0 0 0.5rem 0;
      color: #666;
    }

    .activity-time {
      font-size: 0.8rem;
      color: #999;
    }

    @media (max-width: 768px) {
      .dashboard-grid {
        grid-template-columns: 1fr !important;
      }
    }
  `]
})
export class DashboardComponent { }
