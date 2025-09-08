import { Routes } from '@angular/router';

export const appointmentsRoutes: Routes = [
  {
    path: '',
    loadComponent: () => import('./appointments.component').then(m => m.AppointmentsComponent),
    children: [
      {
        path: '',
        redirectTo: 'list',
        pathMatch: 'full'
      },
      {
        path: 'list',
        loadComponent: () => import('./components/appointments-list/appointments-list.component').then(m => m.AppointmentsListComponent)
      },
      {
        path: 'book',
        loadComponent: () => import('./components/book-appointment/book-appointment.component').then(m => m.BookAppointmentComponent)
      }
    ]
  }
];
