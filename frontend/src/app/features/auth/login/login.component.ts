import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  form = { identifier: '', password: '' };
  loading = signal(false);
  error = signal('');
  showPass = signal(false);

  constructor(private authService: AuthService, private router: Router) { }

  onLogin(): void {
    this.loading.set(true);
    this.error.set('');
    this.authService.login(this.form).subscribe({
      next: () => this.router.navigate(['/feed']),
      error: (err) => {
        this.error.set(err.error?.message || 'Invalid credentials');
        this.loading.set(false);
      }
    });
  }
}
