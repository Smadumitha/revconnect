import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  securityQuestions = [
    'What was your first pet\'s name?',
    'In what city were you born?',
    'What is your mother\'s maiden name?',
    'What was the name of your first school?',
    'What is your favorite book?'
  ];

  form: {
    username: string; email: string; password: string;
    displayName: string; role: 'PERSONAL' | 'CREATOR' | 'BUSINESS';
    privateAccount: boolean;
    securityQuestion: string; securityAnswer: string;
  } = {
      username: '', email: '', password: '',
      displayName: '', role: 'PERSONAL',
      privateAccount: false,
      securityQuestion: '', securityAnswer: ''
    };
  loading = signal(false);
  error = signal('');
  roles = [
    { value: 'PERSONAL', label: 'Personal', icon: 'fas fa-user' },
    { value: 'CREATOR', label: 'Creator', icon: 'fas fa-star' },
    { value: 'BUSINESS', label: 'Business', icon: 'fas fa-briefcase' }
  ] as const;

  setRole(value: string): void {
    this.form.role = value as 'PERSONAL' | 'CREATOR' | 'BUSINESS';
  }

  constructor(private authService: AuthService, private router: Router) { }

  onRegister(): void {
    this.loading.set(true);
    this.error.set('');
    this.authService.register(this.form).subscribe({
      next: () => this.router.navigate(['/feed']),
      error: (err) => {
        this.error.set(err.error?.message || 'Registration failed');
        this.loading.set(false);
      }
    });
  }
}
