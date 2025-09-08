import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { ApiService } from '../../../core/services/api.service';

export interface CreateAppointmentRequest {
  serviceType: string;
  specialty: string;
  date: string;
  time: string;
  reason: string;
  notes?: string;
}

export interface AppointmentResponse {
  id: number;
  userId: number;
  serviceType: string;
  specialty: string;
  date: string;
  time: string;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';
  reason: string;
  notes?: string;
  doctor?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AppointmentListResponse {
  content: AppointmentResponse[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {

  constructor(private apiService: ApiService) { }

  /**
   * Create a new appointment
   */
  createAppointment(appointmentData: CreateAppointmentRequest): Observable<AppointmentResponse> {
    return this.apiService.post<AppointmentResponse>('/appointments', appointmentData);
  }

  /**
   * Get user's appointments with pagination
   */
  getUserAppointments(page: number = 0, size: number = 10): Observable<AppointmentListResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.apiService.get<AppointmentListResponse>('/appointments/user', params);
  }

  /**
   * Get appointment by ID
   */
  getAppointmentById(id: number): Observable<AppointmentResponse> {
    return this.apiService.get<AppointmentResponse>(`/appointments/${id}`);
  }

  /**
   * Update appointment
   */
  updateAppointment(id: number, appointmentData: Partial<CreateAppointmentRequest>): Observable<AppointmentResponse> {
    return this.apiService.put<AppointmentResponse>(`/appointments/${id}`, appointmentData);
  }

  /**
   * Cancel appointment
   */
  cancelAppointment(id: number): Observable<void> {
    return this.apiService.delete<void>(`/appointments/${id}`);
  }

  /**
   * Get available time slots for a specific date and specialty
   */
  getAvailableSlots(date: string, specialty: string): Observable<string[]> {
    const params = new HttpParams()
      .set('date', date)
      .set('specialty', specialty);
    return this.apiService.get<string[]>('/appointments/available-slots', params);
  }

  /**
   * Get all available specialties
   */
  getSpecialties(): Observable<{code: string, name: string}[]> {
    return this.apiService.get<{code: string, name: string}[]>('/appointments/specialties');
  }

  /**
   * Get upcoming appointments (next 7 days)
   */
  getUpcomingAppointments(): Observable<AppointmentResponse[]> {
    return this.apiService.get<AppointmentResponse[]>('/appointments/upcoming');
  }

  /**
   * Reschedule appointment
   */
  rescheduleAppointment(id: number, newDate: string, newTime: string): Observable<AppointmentResponse> {
    const data = { date: newDate, time: newTime };
    return this.apiService.patch<AppointmentResponse>(`/appointments/${id}/reschedule`, data);
  }

  /**
   * Confirm appointment
   */
  confirmAppointment(id: number): Observable<AppointmentResponse> {
    return this.apiService.patch<AppointmentResponse>(`/appointments/${id}/confirm`, {});
  }
}
