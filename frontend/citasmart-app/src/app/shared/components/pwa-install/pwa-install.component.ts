import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatBadgeModule } from '@angular/material/badge';
import { MatDividerModule } from '@angular/material/divider';
import { PwaService } from '../../../core/services/pwa.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-pwa-install',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatBadgeModule,
    MatDividerModule
  ],
  template: `
    <mat-card class="pwa-card">
      <mat-card-header>
        <mat-card-title class="title">
          <mat-icon class="title-icon">install_mobile</mat-icon>
          Instalar CitaSmart
        </mat-card-title>
        <mat-card-subtitle>
          Instala la aplicación para una mejor experiencia
        </mat-card-subtitle>
      </mat-card-header>

      <mat-card-content>
        <!-- PWA Status -->
        <div class="status-section">
          <div class="status-item">
            <mat-icon [ngClass]="(isOnline$ | async) ? 'online' : 'offline'">
              {{ (isOnline$ | async) ? 'wifi' : 'wifi_off' }}
            </mat-icon>
            <span>Estado de conexión: {{ (isOnline$ | async) ? 'En línea' : 'Sin conexión' }}</span>
          </div>

          <div class="status-item">
            <mat-icon [ngClass]="isStandalone ? 'installed' : 'not-installed'">
              {{ isStandalone ? 'check_circle' : 'download' }}
            </mat-icon>
            <span>{{ isStandalone ? 'Aplicación instalada' : 'No instalada' }}</span>
          </div>

          <div class="status-item">
            <mat-icon class="cache-icon">storage</mat-icon>
            <span>Cache: {{ cacheInfo.keys }} elementos ({{ cacheInfo.estimatedSize }})</span>
          </div>
        </div>

        <mat-divider></mat-divider>

        <!-- Actions -->
        <div class="actions-section">
          <button 
            mat-raised-button 
            color="primary"
            *ngIf="canInstall"
            (click)="installApp()"
            class="action-button">
            <mat-icon>install_mobile</mat-icon>
            Instalar aplicación
          </button>

          <button 
            mat-stroked-button
            color="accent"
            (click)="clearCache()"
            class="action-button">
            <mat-icon>clear_all</mat-icon>
            Limpiar cache offline
          </button>

          <button 
            mat-stroked-button
            (click)="refreshCacheInfo()"
            class="action-button">
            <mat-icon>refresh</mat-icon>
            Actualizar información
          </button>
        </div>

        <!-- Features -->
        <div class="features-section">
          <h4>Beneficios de la instalación:</h4>
          <ul>
            <li>
              <mat-icon>offline_bolt</mat-icon>
              Funciona sin conexión
            </li>
            <li>
              <mat-icon>speed</mat-icon>
              Carga más rápido
            </li>
            <li>
              <mat-icon>notifications</mat-icon>
              Notificaciones push
            </li>
            <li>
              <mat-icon>home</mat-icon>
              Acceso desde pantalla de inicio
            </li>
          </ul>
        </div>
      </mat-card-content>
    </mat-card>
  `,
  styles: [`
    .pwa-card {
      max-width: 500px;
      margin: 20px auto;
    }

    .title {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .title-icon {
      color: var(--primary-color, #673ab7);
    }

    .status-section {
      margin: 16px 0;
    }

    .status-item {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;
      padding: 8px;
      border-radius: 8px;
      background-color: rgba(103, 58, 183, 0.05);
    }

    .status-item mat-icon {
      width: 20px;
      height: 20px;
      font-size: 20px;
    }

    .online {
      color: #4caf50;
    }

    .offline {
      color: #f44336;
    }

    .installed {
      color: #4caf50;
    }

    .not-installed {
      color: #ff9800;
    }

    .cache-icon {
      color: #2196f3;
    }

    .actions-section {
      margin: 20px 0;
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .action-button {
      width: 100%;
      height: 48px;
    }

    .action-button mat-icon {
      margin-right: 8px;
    }

    .features-section {
      margin-top: 20px;
    }

    .features-section h4 {
      margin-bottom: 12px;
      color: var(--primary-color, #673ab7);
    }

    .features-section ul {
      list-style: none;
      padding: 0;
    }

    .features-section li {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 8px;
      padding: 6px 0;
    }

    .features-section li mat-icon {
      color: var(--accent-color, #4caf50);
      width: 20px;
      height: 20px;
      font-size: 20px;
    }

    mat-divider {
      margin: 16px 0;
    }

    @media (max-width: 600px) {
      .pwa-card {
        margin: 10px;
        max-width: none;
      }
    }
  `]
})
export class PwaInstallComponent implements OnInit {
  isOnline$: Observable<boolean>;
  isStandalone: boolean = false;
  canInstall: boolean = false;
  cacheInfo = { keys: 0, estimatedSize: '0 Bytes' };

  constructor(private pwaService: PwaService) {
    this.isOnline$ = this.pwaService.isOnline$;
  }

  ngOnInit() {
    this.isStandalone = this.pwaService.isStandalone();
    this.canInstall = this.pwaService.canInstall();
    this.refreshCacheInfo();
  }

  async installApp() {
    const installed = await this.pwaService.promptInstall();
    if (installed) {
      this.isStandalone = true;
      this.canInstall = false;
    }
  }

  clearCache() {
    this.pwaService.clearOfflineCache();
    this.refreshCacheInfo();
  }

  refreshCacheInfo() {
    this.cacheInfo = this.pwaService.getCacheInfo();
    this.canInstall = this.pwaService.canInstall();
  }
}
