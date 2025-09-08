import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatStepperModule } from '@angular/material/stepper';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AppointmentService, CreateAppointmentRequest } from '../../services/appointment.service';

@Component({
  selector: 'app-book-appointment',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatStepperModule,
    MatIconModule,
    MatSnackBarModule
  ],
  template: `
    <div class="book-appointment-container">
      <mat-card>
        <mat-card-header>
          <mat-card-title>Agendar Nueva Cita</mat-card-title>
          <mat-card-subtitle>Completa los siguientes pasos para agendar tu cita</mat-card-subtitle>
        </mat-card-header>

        <mat-card-content>
          <mat-stepper [linear]="true" #stepper>
            <!-- Paso 1: Seleccionar Servicio -->
            <mat-step [stepControl]="serviceForm" label="Servicio">
              <form [formGroup]="serviceForm">
                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>Tipo de Servicio</mat-label>
                  <mat-select formControlName="serviceType" required>
                    <mat-option value="consulta">Consulta General</mat-option>
                    <mat-option value="especialista">Especialista</mat-option>
                    <mat-option value="examen">Examen Médico</mat-option>
                    <mat-option value="control">Control de Rutina</mat-option>
                  </mat-select>
                </mat-form-field>

                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>Especialidad</mat-label>
                  <mat-select formControlName="specialty" required>
                    <mat-option *ngFor="let specialty of specialties" [value]="specialty.code">
                      {{specialty.name}}
                    </mat-option>
                  </mat-select>
                </mat-form-field>

                <div class="step-actions">
                  <button mat-raised-button color="primary" matStepperNext 
                          [disabled]="serviceForm.invalid">
                    Siguiente
                  </button>
                </div>
              </form>
            </mat-step>

            <!-- Paso 2: Seleccionar Fecha y Hora -->
            <mat-step [stepControl]="dateTimeForm" label="Fecha y Hora">
              <form [formGroup]="dateTimeForm">
                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>Fecha</mat-label>
                  <input matInput [matDatepicker]="picker" formControlName="date" required>
                  <mat-hint>DD/MM/AAAA</mat-hint>
                  <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
                  <mat-datepicker #picker></mat-datepicker>
                </mat-form-field>

                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>Hora</mat-label>
                  <mat-select formControlName="time" required>
                    <mat-option *ngFor="let slot of availableSlots" [value]="slot">
                      {{slot}}
                    </mat-option>
                  </mat-select>
                </mat-form-field>

                <div class="step-actions">
                  <button mat-button matStepperPrevious>Anterior</button>
                  <button mat-raised-button color="primary" matStepperNext 
                          [disabled]="dateTimeForm.invalid">
                    Siguiente
                  </button>
                </div>
              </form>
            </mat-step>

            <!-- Paso 3: Información Adicional -->
            <mat-step [stepControl]="additionalInfoForm" label="Información Adicional">
              <form [formGroup]="additionalInfoForm">
                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>Motivo de la Consulta</mat-label>
                  <textarea matInput rows="4" formControlName="reason" 
                            placeholder="Describe brevemente el motivo de tu consulta"></textarea>
                </mat-form-field>

                <mat-form-field appearance="outline" class="full-width">
                  <mat-label>Observaciones (Opcional)</mat-label>
                  <textarea matInput rows="3" formControlName="notes" 
                            placeholder="Información adicional que consideres importante"></textarea>
                </mat-form-field>

                <div class="step-actions">
                  <button mat-button matStepperPrevious>Anterior</button>
                  <button mat-raised-button color="primary" matStepperNext 
                          [disabled]="additionalInfoForm.invalid">
                    Siguiente
                  </button>
                </div>
              </form>
            </mat-step>

            <!-- Paso 4: Confirmación -->
            <mat-step label="Confirmación">
              <div class="confirmation-summary">
                <h3>Resumen de tu Cita</h3>
                
                <div class="summary-item">
                  <strong>Servicio:</strong> {{getServiceTypeLabel()}}
                </div>
                <div class="summary-item">
                  <strong>Especialidad:</strong> {{getSpecialtyLabel()}}
                </div>
                <div class="summary-item">
                  <strong>Fecha:</strong> {{getFormattedDate()}}
                </div>
                <div class="summary-item">
                  <strong>Hora:</strong> {{dateTimeForm.get('time')?.value}}
                </div>
                <div class="summary-item">
                  <strong>Motivo:</strong> {{additionalInfoForm.get('reason')?.value}}
                </div>
                <div class="summary-item" *ngIf="additionalInfoForm.get('notes')?.value">
                  <strong>Observaciones:</strong> {{additionalInfoForm.get('notes')?.value}}
                </div>
              </div>

              <div class="step-actions">
                <button mat-button matStepperPrevious>Anterior</button>
                <button mat-raised-button color="primary" (click)="confirmAppointment()" 
                        [disabled]="isSubmitting">
                  {{isSubmitting ? 'Agendando...' : 'Confirmar Cita'}}
                </button>
              </div>
            </mat-step>
          </mat-stepper>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .book-appointment-container {
      max-width: 800px;
      margin: 2rem auto;
      padding: 1rem;
    }

    .full-width {
      width: 100%;
      margin-bottom: 1rem;
    }

    .step-actions {
      margin-top: 2rem;
      display: flex;
      gap: 1rem;
      justify-content: flex-end;
    }

    .confirmation-summary {
      background: #f5f5f5;
      padding: 1.5rem;
      border-radius: 8px;
      margin-bottom: 2rem;
    }

    .confirmation-summary h3 {
      margin-top: 0;
      color: #333;
    }

    .summary-item {
      margin-bottom: 0.5rem;
      padding: 0.5rem 0;
      border-bottom: 1px solid #e0e0e0;
    }

    .summary-item:last-child {
      border-bottom: none;
    }

    mat-card-header {
      margin-bottom: 2rem;
    }
  `]
})
export class BookAppointmentComponent implements OnInit {
  serviceForm: FormGroup;
  dateTimeForm: FormGroup;
  additionalInfoForm: FormGroup;
  isSubmitting = false;
  availableSlots: string[] = [];
  specialties: {code: string, name: string}[] = [];

  serviceTypes = {
    'consulta': 'Consulta General',
    'especialista': 'Especialista',
    'examen': 'Examen Médico',
    'control': 'Control de Rutina'
  };

  constructor(
    private fb: FormBuilder,
    private appointmentService: AppointmentService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.serviceForm = this.fb.group({
      serviceType: ['', Validators.required],
      specialty: ['', Validators.required]
    });

    this.dateTimeForm = this.fb.group({
      date: ['', Validators.required],
      time: ['', Validators.required]
    });

    this.additionalInfoForm = this.fb.group({
      reason: ['', Validators.required],
      notes: ['']
    });
  }

  ngOnInit() {
    this.loadSpecialties();
    
    // Watch for specialty changes to load available slots
    this.serviceForm.get('specialty')?.valueChanges.subscribe(specialty => {
      if (specialty && this.dateTimeForm.get('date')?.value) {
        this.loadAvailableSlots();
      }
    });

    // Watch for date changes to load available slots  
    this.dateTimeForm.get('date')?.valueChanges.subscribe(date => {
      if (date && this.serviceForm.get('specialty')?.value) {
        this.loadAvailableSlots();
      }
    });
  }

  loadSpecialties() {
    this.appointmentService.getSpecialties().subscribe({
      next: (specialties) => {
        this.specialties = specialties;
      },
      error: (error) => {
        console.error('Error loading specialties:', error);
        // Use fallback specialties
        this.specialties = [
          { code: 'medicina-general', name: 'Medicina General' },
          { code: 'cardiologia', name: 'Cardiología' },
          { code: 'dermatologia', name: 'Dermatología' },
          { code: 'pediatria', name: 'Pediatría' },
          { code: 'ginecologia', name: 'Ginecología' }
        ];
      }
    });
  }

  loadAvailableSlots() {
    const date = this.dateTimeForm.get('date')?.value;
    const specialty = this.serviceForm.get('specialty')?.value;
    
    if (date && specialty) {
      const dateString = date.toISOString().split('T')[0]; // Format as YYYY-MM-DD
      
      this.appointmentService.getAvailableSlots(dateString, specialty).subscribe({
        next: (slots) => {
          this.availableSlots = slots;
        },
        error: (error) => {
          console.error('Error loading available slots:', error);
          // Use fallback slots
          this.availableSlots = [
            '08:00', '09:00', '10:00', '11:00', 
            '14:00', '15:00', '16:00', '17:00'
          ];
        }
      });
    }
  }

  getServiceTypeLabel(): string {
    const value = this.serviceForm.get('serviceType')?.value;
    return this.serviceTypes[value as keyof typeof this.serviceTypes] || value;
  }

  getSpecialtyLabel(): string {
    const value = this.serviceForm.get('specialty')?.value;
    const specialty = this.specialties.find(s => s.code === value);
    return specialty?.name || value;
  }

  getFormattedDate(): string {
    const date = this.dateTimeForm.get('date')?.value;
    if (date) {
      return new Date(date).toLocaleDateString('es-ES');
    }
    return '';
  }

  confirmAppointment() {
    if (this.serviceForm.valid && this.dateTimeForm.valid && this.additionalInfoForm.valid) {
      this.isSubmitting = true;
      
      const appointmentData: CreateAppointmentRequest = {
        serviceType: this.serviceForm.get('serviceType')?.value,
        specialty: this.serviceForm.get('specialty')?.value,
        date: this.dateTimeForm.get('date')?.value.toISOString().split('T')[0],
        time: this.dateTimeForm.get('time')?.value,
        reason: this.additionalInfoForm.get('reason')?.value,
        notes: this.additionalInfoForm.get('notes')?.value
      };

      this.appointmentService.createAppointment(appointmentData).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          this.snackBar.open('¡Cita agendada exitosamente!', 'Cerrar', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.router.navigate(['/appointments']);
        },
        error: (error) => {
          this.isSubmitting = false;
          console.error('Error booking appointment:', error);
          this.snackBar.open('Error al agendar la cita. Intenta nuevamente.', 'Cerrar', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }
}
