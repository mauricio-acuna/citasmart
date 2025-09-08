import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { NotificationService } from './notification.service';

@Injectable({
  providedIn: 'root'
})
export class PwaService {
  private isOnlineSubject = new BehaviorSubject<boolean>(navigator.onLine);
  public isOnline$ = this.isOnlineSubject.asObservable();

  private deferredPrompt: any;

  constructor(
    private notificationService: NotificationService
  ) {
    this.monitorNetworkStatus();
    this.setupInstallPrompt();
  }

  private setupInstallPrompt() {
    window.addEventListener('beforeinstallprompt', (e) => {
      // Prevent Chrome 67 and earlier from automatically showing the prompt
      e.preventDefault();
      // Stash the event so it can be triggered later
      this.deferredPrompt = e;
    });
  }

  private monitorNetworkStatus() {
    window.addEventListener('online', () => {
      this.isOnlineSubject.next(true);
      this.notificationService.success('Conexión restaurada');
    });

    window.addEventListener('offline', () => {
      this.isOnlineSubject.next(false);
      this.notificationService.warning('Sin conexión a internet. Trabajando offline');
    });
  }

  /**
   * Check if the app is running in standalone mode (installed as PWA)
   */
  isStandalone(): boolean {
    return window.matchMedia('(display-mode: standalone)').matches ||
           (window.navigator as any).standalone === true;
  }

  /**
   * Prompt user to install the PWA
   */
  async promptInstall(): Promise<boolean> {
    if (this.deferredPrompt) {
      // Show the prompt
      this.deferredPrompt.prompt();
      
      // Wait for the user to respond to the prompt
      const { outcome } = await this.deferredPrompt.userChoice;
      
      // Clear the deferred prompt
      this.deferredPrompt = null;
      
      return outcome === 'accepted';
    } else {
      this.notificationService.info(
        'Para instalar CitaSmart, usa el menú de tu navegador y selecciona "Instalar aplicación"'
      );
      return false;
    }
  }

  /**
   * Check if install prompt is available
   */
  canInstall(): boolean {
    return !!this.deferredPrompt && !this.isStandalone();
  }

  /**
   * Get network status
   */
  isOnline(): boolean {
    return navigator.onLine;
  }

  /**
   * Cache important data for offline use
   */
  cacheForOffline(data: any, key: string): void {
    try {
      localStorage.setItem(`offline_${key}`, JSON.stringify({
        data,
        timestamp: Date.now()
      }));
    } catch (error) {
      console.warn('Failed to cache data for offline use:', error);
    }
  }

  /**
   * Get cached data for offline use
   */
  getCachedData(key: string, maxAge: number = 24 * 60 * 60 * 1000): any {
    try {
      const cached = localStorage.getItem(`offline_${key}`);
      if (cached) {
        const { data, timestamp } = JSON.parse(cached);
        
        // Check if data is still fresh
        if (Date.now() - timestamp < maxAge) {
          return data;
        } else {
          // Remove stale data
          localStorage.removeItem(`offline_${key}`);
        }
      }
      return null;
    } catch (error) {
      console.warn('Failed to retrieve cached data:', error);
      return null;
    }
  }

  /**
   * Clear offline cache
   */
  clearOfflineCache(): void {
    const keys = Object.keys(localStorage);
    keys.forEach(key => {
      if (key.startsWith('offline_')) {
        localStorage.removeItem(key);
      }
    });
    this.notificationService.success('Cache offline limpiado');
  }

  /**
   * Get cache size information
   */
  getCacheInfo(): { keys: number; estimatedSize: string } {
    const keys = Object.keys(localStorage).filter(key => key.startsWith('offline_'));
    let totalSize = 0;
    
    keys.forEach(key => {
      totalSize += localStorage.getItem(key)?.length || 0;
    });
    
    return {
      keys: keys.length,
      estimatedSize: this.formatBytes(totalSize * 2) // Rough estimate (UTF-16)
    };
  }

  private formatBytes(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }
}
