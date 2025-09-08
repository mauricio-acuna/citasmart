import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Notification {
  id: string;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info';
  timestamp: Date;
  read: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationsSubject = new BehaviorSubject<Notification[]>([]);
  public notifications$ = this.notificationsSubject.asObservable();

  constructor(private snackBar: MatSnackBar) { }

  /**
   * Show a success message
   */
  success(message: string, duration: number = 3000) {
    this.snackBar.open(message, 'Cerrar', {
      duration,
      panelClass: ['success-snackbar'],
      verticalPosition: 'top',
      horizontalPosition: 'end'
    });
    
    this.addNotification(message, 'success');
  }

  /**
   * Show an error message
   */
  error(message: string, duration: number = 5000) {
    this.snackBar.open(message, 'Cerrar', {
      duration,
      panelClass: ['error-snackbar'],
      verticalPosition: 'top',
      horizontalPosition: 'end'
    });
    
    this.addNotification(message, 'error');
  }

  /**
   * Show a warning message
   */
  warning(message: string, duration: number = 4000) {
    this.snackBar.open(message, 'Cerrar', {
      duration,
      panelClass: ['warning-snackbar'],
      verticalPosition: 'top',
      horizontalPosition: 'end'
    });
    
    this.addNotification(message, 'warning');
  }

  /**
   * Show an info message
   */
  info(message: string, duration: number = 3000) {
    this.snackBar.open(message, 'Cerrar', {
      duration,
      panelClass: ['info-snackbar'],
      verticalPosition: 'top',
      horizontalPosition: 'end'
    });
    
    this.addNotification(message, 'info');
  }

  /**
   * Add a notification to the notifications list
   */
  private addNotification(message: string, type: 'success' | 'error' | 'warning' | 'info') {
    const notification: Notification = {
      id: this.generateId(),
      message,
      type,
      timestamp: new Date(),
      read: false
    };

    const currentNotifications = this.notificationsSubject.value;
    this.notificationsSubject.next([notification, ...currentNotifications]);
  }

  /**
   * Mark notification as read
   */
  markAsRead(notificationId: string) {
    const notifications = this.notificationsSubject.value.map(notification =>
      notification.id === notificationId 
        ? { ...notification, read: true }
        : notification
    );
    this.notificationsSubject.next(notifications);
  }

  /**
   * Mark all notifications as read
   */
  markAllAsRead() {
    const notifications = this.notificationsSubject.value.map(notification => ({
      ...notification,
      read: true
    }));
    this.notificationsSubject.next(notifications);
  }

  /**
   * Clear all notifications
   */
  clearAll() {
    this.notificationsSubject.next([]);
  }

  /**
   * Get unread notifications count
   */
  getUnreadCount(): Observable<number> {
    return new Observable(observer => {
      this.notifications$.subscribe(notifications => {
        const unreadCount = notifications.filter(n => !n.read).length;
        observer.next(unreadCount);
      });
    });
  }

  /**
   * Generate a unique ID for notifications
   */
  private generateId(): string {
    return Math.random().toString(36).substr(2, 9);
  }
}
