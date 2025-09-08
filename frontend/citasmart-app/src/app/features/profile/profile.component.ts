import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService, User } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatSnackBarModule
  ],
  template: `
    <div class="profile-container">
      <mat-card class="profile-card">
        <mat-card-header>
          <div mat-card-avatar class="profile-avatar">
            <mat-icon>person</mat-icon>
          </div>
          <mat-card-title>Mi Perfil</mat-card-title>
          <mat-card-subtitle>Gestiona tu información personal</mat-card-subtitle>
        </mat-card-header>

        <mat-card-content>
          <mat-tab-group>
            <!-- Información Personal -->
            <mat-tab label="Información Personal">
              <div class="tab-content">
                <form [formGroup]="profileForm" (ngSubmit)="updateProfile()">
                  <div class="form-row">
                    <mat-form-field appearance="outline" class="half-width">
                      <mat-label>Nombre</mat-label>
                      <input matInput formControlName="firstName" required>
                      <mat-error *ngIf="profileForm.get('firstName')?.hasError('required')">
                        El nombre es requerido
                      </mat-error>
                    </mat-form-field>

                    <mat-form-field appearance="outline" class="half-width">
                      <mat-label>Apellido</mat-label>
                      <input matInput formControlName="lastName" required>
                      <mat-error *ngIf="profileForm.get('lastName')?.hasError('required')">
                        El apellido es requerido
                      </mat-error>
                    </mat-form-field>
                  </div>

                  <mat-form-field appearance="outline" class="full-width">
                    <mat-label>Email</mat-label>
                    <input matInput type="email" formControlName="email" required>
                    <mat-icon matSuffix>email</mat-icon>
                    <mat-error *ngIf="profileForm.get('email')?.hasError('required')">
                      El email es requerido
                    </mat-error>
                    <mat-error *ngIf="profileForm.get('email')?.hasError('email')">
                      Ingresa un email válido
                    </mat-error>
                  </mat-form-field>

                  <mat-form-field appearance="outline" class="full-width">
                    <mat-label>Teléfono</mat-label>
                    <input matInput formControlName="phone" required>
                    <mat-icon matSuffix>phone</mat-icon>
                    <mat-error *ngIf="profileForm.get('phone')?.hasError('required')">
                      El teléfono es requerido
                    </mat-error>
                  </mat-form-field>

                  <mat-form-field appearance="outline" class="full-width">
                    <mat-label>Fecha de Nacimiento</mat-label>
                    <input matInput type="date" formControlName="dateOfBirth">
                  </mat-form-field>

                  <mat-form-field appearance="outline" class="full-width">
                    <mat-label>Dirección</mat-label>
                    <textarea matInput rows="3" formControlName="address" 
                              placeholder="Tu dirección completa"></textarea>
                  </mat-form-field>

                  <div class="form-actions">
                    <button mat-raised-button color="primary" type="submit" 
                            [disabled]="profileForm.invalid || isUpdating">
                      {{isUpdating ? 'Actualizando...' : 'Actualizar Perfil'}}
                    </button>
                  </div>
                </form>
              </div>
            </mat-tab>

            <!-- Cambiar Contraseña -->
            <mat-tab label="Seguridad">
              <div class="tab-content">
                <form [formGroup]="passwordForm" (ngSubmit)="changePassword()">
                  <mat-form-field appearance="outline" class="full-width">
                    <mat-label>Contraseña Actual</mat-label>
                    <input matInput [type]="hideCurrentPassword ? 'password' : 'text'" 
                           formControlName="currentPassword" required>
                    <button mat-icon-button matSuffix 
                            (click)="hideCurrentPassword = !hideCurrentPassword" type="button">
                      <mat-icon>{{hideCurrentPassword ? 'visibility_off' : 'visibility'}}</mat-icon>
                    </button>
                    <mat-error *ngIf="passwordForm.get('currentPassword')?.hasError('required')">
                      La contraseña actual es requerida
                    </mat-error>
                  </mat-form-field>

                  <mat-form-field appearance="outline" class="full-width">
                    <mat-label>Nueva Contraseña</mat-label>
                    <input matInput [type]="hideNewPassword ? 'password' : 'text'" 
                           formControlName="newPassword" required>
                    <button mat-icon-button matSuffix 
                            (click)="hideNewPassword = !hideNewPassword" type="button">
                      <mat-icon>{{hideNewPassword ? 'visibility_off' : 'visibility'}}</mat-icon>
                    </button>
                    <mat-error *ngIf="passwordForm.get('newPassword')?.hasError('required')">
                      La nueva contraseña es requerida
                    </mat-error>
                    <mat-error *ngIf="passwordForm.get('newPassword')?.hasError('minlength')">
                      La contraseña debe tener al menos 6 caracteres
                    </mat-error>
                  </mat-form-field>

                  <mat-form-field appearance="outline" class="full-width">
                    <mat-label>Confirmar Nueva Contraseña</mat-label>
                    <input matInput [type]="hideConfirmPassword ? 'password' : 'text'" 
                           formControlName="confirmPassword" required>
                    <button mat-icon-button matSuffix 
                            (click)="hideConfirmPassword = !hideConfirmPassword" type="button">
                      <mat-icon>{{hideConfirmPassword ? 'visibility_off' : 'visibility'}}</mat-icon>
                    </button>
                    <mat-error *ngIf="passwordForm.get('confirmPassword')?.hasError('required')">
                      Confirma tu nueva contraseña
                    </mat-error>
                    <mat-error *ngIf="passwordForm.hasError('passwordMismatch')">
                      Las contraseñas no coinciden
                    </mat-error>
                  </mat-form-field>

                  <div class="form-actions">
                    <button mat-raised-button color="primary" type="submit" 
                            [disabled]="passwordForm.invalid || isChangingPassword">
                      {{isChangingPassword ? 'Cambiando...' : 'Cambiar Contraseña'}}
                    </button>
                  </div>
                </form>
              </div>
            </mat-tab>

            <!-- Preferencias -->
            <mat-tab label="Preferencias">
              <div class="tab-content">
                <h3>Próximamente</h3>
                <p>Esta sección estará disponible en futuras versiones para configurar tus preferencias de notificaciones y otros ajustes.</p>
              </div>
            </mat-tab>
          </mat-tab-group>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .profile-container {
      max-width: 800px;
      margin: 2rem auto;
      padding: 1rem;
    }

    .profile-card {
      margin-bottom: 2rem;
    }

    .profile-avatar {
      background-color: #673ab7;
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .tab-content {
      padding: 2rem 0;
    }

    .form-row {
      display: flex;
      gap: 1rem;
    }

    .half-width {
      flex: 1;
      margin-bottom: 1rem;
    }

    .full-width {
      width: 100%;
      margin-bottom: 1rem;
    }

    .form-actions {
      margin-top: 1rem;
      display: flex;
      justify-content: flex-end;
    }

    @media (max-width: 768px) {
      .form-row {
        flex-direction: column;
      }
      
      .half-width {
        width: 100%;
      }
    }
  `]
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup;
  passwordForm: FormGroup;
  isUpdating = false;
  isChangingPassword = false;
  hideCurrentPassword = true;
  hideNewPassword = true;
  hideConfirmPassword = true;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.profileForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required]],
      dateOfBirth: [''],
      address: ['']
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit() {
    this.loadUserProfile();
  }

  loadUserProfile() {
    // TODO: Load user profile from API
    // For now, we'll use mock data
    const mockUser = {
      firstName: 'Juan',
      lastName: 'Pérez',
      email: 'juan.perez@example.com',
      phone: '+56 9 1234 5678',
      dateOfBirth: '1990-01-15',
      address: 'Av. Las Condes 123, Santiago'
    };

    this.profileForm.patchValue(mockUser);
  }

  passwordMatchValidator(form: FormGroup) {
    const newPassword = form.get('newPassword');
    const confirmPassword = form.get('confirmPassword');
    
    if (newPassword && confirmPassword && newPassword.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    return null;
  }

  updateProfile() {
    if (this.profileForm.valid) {
      this.isUpdating = true;
      const profileData = this.profileForm.value;
      
      // TODO: Call API to update profile
      console.log('Updating profile:', profileData);
      
      // Simulate API call
      setTimeout(() => {
        this.isUpdating = false;
        this.snackBar.open('Perfil actualizado exitosamente', 'Cerrar', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
      }, 2000);
    }
  }

  changePassword() {
    if (this.passwordForm.valid) {
      this.isChangingPassword = true;
      const passwordData = {
        currentPassword: this.passwordForm.get('currentPassword')?.value,
        newPassword: this.passwordForm.get('newPassword')?.value
      };
      
      // TODO: Call API to change password
      console.log('Changing password:', passwordData);
      
      // Simulate API call
      setTimeout(() => {
        this.isChangingPassword = false;
        this.passwordForm.reset();
        this.snackBar.open('Contraseña cambiada exitosamente', 'Cerrar', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
      }, 2000);
    }
  }
}
