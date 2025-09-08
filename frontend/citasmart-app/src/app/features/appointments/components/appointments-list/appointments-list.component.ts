import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';

interface Appointment {
  id: number;
  date: string;
  time: string;
  service: string;
  specialty: string;
  doctor: string;
  status: 'confirmed' | 'pending' | 'cancelled' | 'completed';
  reason: string;
}

@Component({
  selector: 'app-appointments-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatMenuModule
  ],
  template: `
    <div class="appointments-list-container">
      <div class="list-header">
        <h1>Mis Citas</h1>
        <button mat-raised-button color="primary" routerLink="/appointments/book">
          <mat-icon>add</mat-icon>
          Nueva Cita
        </button>
      </div>

      <div class="appointments-grid" *ngIf="appointments.length > 0">
        <mat-card *ngFor="let appointment of appointments" class="appointment-card">
          <mat-card-header>
            <div mat-card-avatar class="appointment-avatar">
              <mat-icon>event</mat-icon>
            </div>
            <mat-card-title>{{appointment.service}}</mat-card-title>
            <mat-card-subtitle>{{appointment.specialty}}</mat-card-subtitle>
            <div class="card-actions">
              <button mat-icon-button [matMenuTriggerFor]="menu">
                <mat-icon>more_vert</mat-icon>
              </button>
              <mat-menu #menu="matMenu">
                <button mat-menu-item *ngIf="appointment.status === 'confirmed'">
                  <mat-icon>edit</mat-icon>
                  Reprogramar
                </button>
                <button mat-menu-item *ngIf="appointment.status !== 'cancelled'">
                  <mat-icon>cancel</mat-icon>
                  Cancelar
                </button>
                <button mat-menu-item>
                  <mat-icon>info</mat-icon>
                  Ver Detalles
                </button>
              </mat-menu>
            </div>
          </mat-card-header>

          <mat-card-content>
            <div class="appointment-details">
              <div class="detail-item">
                <mat-icon>calendar_today</mat-icon>
                <span>{{formatDate(appointment.date)}}</span>
              </div>
              <div class="detail-item">
                <mat-icon>access_time</mat-icon>
                <span>{{appointment.time}}</span>
              </div>
              <div class="detail-item">
                <mat-icon>person</mat-icon>
                <span>{{appointment.doctor}}</span>
              </div>
              <div class="detail-item">
                <mat-icon>notes</mat-icon>
                <span>{{appointment.reason}}</span>
              </div>
            </div>

            <mat-chip-listbox class="status-chip">
              <mat-chip-option [selected]="true" [color]="getStatusColor(appointment.status)">
                {{getStatusLabel(appointment.status)}}
              </mat-chip-option>
            </mat-chip-listbox>
          </mat-card-content>

          <mat-card-actions *ngIf="appointment.status === 'confirmed'">
            <button mat-button color="primary">
              <mat-icon>video_call</mat-icon>
              Unirse a la consulta
            </button>
          </mat-card-actions>
        </mat-card>
      </div>

      <div class="empty-state" *ngIf="appointments.length === 0">
        <mat-icon class="empty-icon">event_busy</mat-icon>
        <h2>No tienes citas programadas</h2>
        <p>Agenda tu primera cita para comenzar</p>
        <button mat-raised-button color="primary" routerLink="/appointments/book">
          Agendar Primera Cita
        </button>
      </div>
    </div>
  `,
  styles: [`
    .appointments-list-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 2rem;
    }

    .list-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    .list-header h1 {
      margin: 0;
      color: #333;
    }

    .appointments-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
      gap: 1.5rem;
    }

    .appointment-card {
      transition: box-shadow 0.3s ease;
    }

    .appointment-card:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    .appointment-avatar {
      background-color: #673ab7;
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .card-actions {
      margin-left: auto;
    }

    .appointment-details {
      margin-bottom: 1rem;
    }

    .detail-item {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      margin-bottom: 0.5rem;
      font-size: 0.9rem;
      color: #666;
    }

    .detail-item mat-icon {
      font-size: 18px;
      width: 18px;
      height: 18px;
      color: #999;
    }

    .status-chip {
      margin-top: 1rem;
    }

    .empty-state {
      text-align: center;
      padding: 4rem 2rem;
      color: #666;
    }

    .empty-icon {
      font-size: 4rem;
      width: 4rem;
      height: 4rem;
      margin-bottom: 1rem;
      color: #ccc;
    }

    .empty-state h2 {
      margin: 1rem 0;
      color: #333;
    }

    @media (max-width: 768px) {
      .appointments-grid {
        grid-template-columns: 1fr;
      }
      
      .list-header {
        flex-direction: column;
        gap: 1rem;
        align-items: stretch;
      }
    }
  `]
})
export class AppointmentsListComponent {
  appointments: Appointment[] = [
    {
      id: 1,
      date: '2024-01-15',
      time: '10:00 AM',
      service: 'Consulta General',
      specialty: 'Medicina General',
      doctor: 'Dr. Juan Pérez',
      status: 'confirmed',
      reason: 'Consulta de rutina'
    },
    {
      id: 2,
      date: '2024-01-20',
      time: '02:30 PM',
      service: 'Especialista',
      specialty: 'Cardiología',
      doctor: 'Dra. María González',
      status: 'pending',
      reason: 'Control cardiovascular'
    },
    {
      id: 3,
      date: '2024-01-10',
      time: '09:00 AM',
      service: 'Examen Médico',
      specialty: 'Medicina General',
      doctor: 'Dr. Carlos López',
      status: 'completed',
      reason: 'Examen anual completo'
    }
  ];

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  getStatusLabel(status: string): string {
    const labels = {
      'confirmed': 'Confirmada',
      'pending': 'Pendiente',
      'cancelled': 'Cancelada',
      'completed': 'Completada'
    };
    return labels[status as keyof typeof labels] || status;
  }

  getStatusColor(status: string): 'primary' | 'accent' | 'warn' {
    const colors = {
      'confirmed': 'primary' as const,
      'pending': 'accent' as const,
      'cancelled': 'warn' as const,
      'completed': 'primary' as const
    };
    return colors[status as keyof typeof colors] || 'primary';
  }
}
