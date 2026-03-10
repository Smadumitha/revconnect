import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {
  step = signal(1);
  email = '';
  securityQuestion = signal('');
  answer = '';
  newPassword = '';
  loading = signal(false);
  error = signal('');
  success = signal('');

  constructor(private authService: AuthService, private router: Router) { }

  // Step 1: fetch security question by username (getSecurityQuestion returns plain string)
  getQuestion() {
    if (!this.email) return;
    this.loading.set(true);
    this.error.set('');
    this.authService.getSecurityQuestion(this.email).subscribe({
      next: (question: string) => {
        this.securityQuestion.set(question);
        this.step.set(2);
        this.loading.set(false);
      },
      error: (err: any) => {
        this.error.set(err.error?.message || 'Username not found');
        this.loading.set(false);
      }
    });
  }

  // Step 2: validate answer then reset password
  resetPassword() {
    if (!this.answer || !this.newPassword) return;
    this.loading.set(true);
    this.error.set('');
    // First validate the security answer
    this.authService.validateSecurityAnswer({
      username: this.email,
      answer: this.answer
    }).subscribe({
      next: (_: string) => {
        // Answer verified — now reset the password
        this.authService.resetPassword({
          username: this.email,
          newPassword: this.newPassword
        }).subscribe({
          next: (__: string) => {
            this.success.set('Password reset successfully!');
            this.loading.set(false);
            setTimeout(() => this.router.navigate(['/auth/login']), 2000);
          },
          error: (err: any) => {
            this.error.set(err.error?.message || 'Reset failed');
            this.loading.set(false);
          }
        });
      },
      error: (err: any) => {
        this.error.set(err.error?.message || 'Incorrect answer');
        this.loading.set(false);
      }
    });
  }
}
