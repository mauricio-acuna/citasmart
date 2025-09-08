import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { PwaService } from '../services/pwa.service';
import { NotificationService } from '../services/notification.service';

@Injectable()
export class OfflineCacheInterceptor implements HttpInterceptor {

  constructor(
    private pwaService: PwaService,
    private notificationService: NotificationService
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<any> {
    // Only cache GET requests for specific endpoints
    if (req.method === 'GET' && this.shouldCache(req.url)) {
      return this.handleCachableRequest(req, next);
    }

    // For non-cachable requests, handle offline scenarios
    if (!this.pwaService.isOnline()) {
      this.notificationService.warning('Sin conexión. La acción se realizará cuando vuelva la conexión.');
      return throwError(() => new Error('Offline - Request not cached'));
    }

    return next.handle(req);
  }

  private shouldCache(url: string): boolean {
    // Cache specific API endpoints that are safe to cache
    const cacheableEndpoints = [
      '/api/appointments',
      '/api/profile',
      '/api/services',
      '/api/professionals'
    ];
    
    return cacheableEndpoints.some(endpoint => url.includes(endpoint));
  }

  private handleCachableRequest(req: HttpRequest<any>, next: HttpHandler): Observable<any> {
    const cacheKey = this.getCacheKey(req);

    if (!this.pwaService.isOnline()) {
      // Try to get from cache when offline
      const cachedData = this.pwaService.getCachedData(cacheKey);
      if (cachedData) {
        console.log('Serving from cache (offline):', cacheKey);
        return of(new HttpResponse({
          body: cachedData,
          status: 200,
          statusText: 'OK (from cache)'
        }));
      } else {
        this.notificationService.error('No hay datos disponibles sin conexión');
        return throwError(() => new Error('No cached data available'));
      }
    }

    // When online, make the request and cache the response
    return next.handle(req).pipe(
      tap(event => {
        if (event instanceof HttpResponse) {
          // Cache successful responses
          this.pwaService.cacheForOffline(event.body, cacheKey);
          console.log('Cached response for:', cacheKey);
        }
      }),
      catchError((error: HttpErrorResponse) => {
        // If online request fails, try cache as fallback
        const cachedData = this.pwaService.getCachedData(cacheKey);
        if (cachedData) {
          console.log('Network failed, serving from cache:', cacheKey);
          this.notificationService.info('Mostrando datos almacenados (sin conexión)');
          return of(new HttpResponse({
            body: cachedData,
            status: 200,
            statusText: 'OK (from cache - network failed)'
          }));
        }
        return throwError(() => error);
      })
    );
  }

  private getCacheKey(req: HttpRequest<any>): string {
    // Create a unique cache key based on URL and relevant parameters
    const url = req.url.split('?')[0]; // Remove query params for base key
    return `http_${btoa(url).replace(/[^a-zA-Z0-9]/g, '')}`; // Safe key for localStorage
  }
}
