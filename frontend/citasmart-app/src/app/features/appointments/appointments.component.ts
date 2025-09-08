import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-appointments',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  template: `
    <div class="appointments-container">
      <router-outlet></router-outlet>
    </div>
  `,
  styles: [`
    .appointments-container {
      padding: 1rem;
    }
  `]
})
export class AppointmentsComponent { }
