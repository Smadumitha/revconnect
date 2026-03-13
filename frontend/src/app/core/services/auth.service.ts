import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResponse } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'rc_token';
  private readonly REFRESH_KEY = 'rc_refresh_token';
  private readonly USER_KEY = 'rc_user';

  currentUser = signal<AuthResponse | null>(this.getStoredUser());
  isLoggedIn = signal<boolean>(!!this.getStoredToken());

  constructor(private http: HttpClient, private router: Router) { }

  // ── REGISTER ──────────────────────────────────────────────
  // POST /auth/register
  // Body: { email, username, password, role, privateAccount, securityQuestion, securityAnswer }
  // Enhanced: backend now validates @NotBlank, @Email, @Size
  register(data: {
    email: string;
    username: string;
    password: string;
    role: 'PERSONAL' | 'CREATOR' | 'BUSINESS';
    privateAccount: boolean;
    securityQuestion: string;
    securityAnswer: string;
  }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/auth/register', data).pipe(
      tap(res => {
        // Backend only returns tokens. We need to fetch the profile to get the userId.
        this.fetchProfileAndStore(res, data.username);
      })
    );
  }

  // ── LOGIN ─────────────────────────────────────────────────
  // POST /auth/login
  // Body: { identifier (username OR email), password }
  login(data: { identifier: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/auth/login', data).pipe(
      tap(res => {
        // If the identifier doesn't look like an email, assume it's a username.
        // This is a workaround since the backend doesn't return the userId in AuthResponse.
        const username = data.identifier.includes('@') ? '' : data.identifier;
        if (username) {
          this.fetchProfileAndStore(res, username);
        } else {
          this.storeAuth(res);
        }
      })
    );
  }

  // ── LOGOUT ────────────────────────────────────────────────
  // POST /auth/logout
  // Body: refreshToken string — invalidates the token in the DB
  logout(): void {
    const refreshToken = localStorage.getItem(this.REFRESH_KEY);
    if (refreshToken) {
      this.http.post('/auth/logout', { refreshToken }, { responseType: 'text' })
        .subscribe({ error: () => console.log('Session locally cleared.') });
    }
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUser.set(null);
    this.isLoggedIn.set(false);
    this.router.navigate(['/auth/login']);
  }

  // ── REFRESH TOKEN ─────────────────────────────────────────
  // POST /auth/refresh
  // Body: refreshToken string
  refreshToken(): Observable<AuthResponse> {
    const token = localStorage.getItem(this.REFRESH_KEY) || '';
    return this.http.post<AuthResponse>('/auth/refresh', token).pipe(
      tap(res => this.storeAuth(res))
    );
  }

  // ── GET SECURITY QUESTION ─────────────────────────────────
  // GET /auth/security-question/{username}
  getSecurityQuestion(username: string): Observable<string> {
    return this.http.get(`/auth/security-question/${username}`, { responseType: 'text' });
  }

  // ── VALIDATE SECURITY ANSWER ──────────────────────────────
  // POST /auth/validate-answer
  // Body: { username, answer }
  validateSecurityAnswer(data: { username: string; answer: string }): Observable<string> {
    return this.http.post('/auth/validate-answer', data, { responseType: 'text' });
  }

  // ── RESET PASSWORD ────────────────────────────────────────
  // POST /auth/reset-password
  // Body: { username, newPassword }
  resetPassword(data: { username: string; newPassword: string }): Observable<string> {
    return this.http.post('/auth/reset-password', data, { responseType: 'text' });
  }

  // ── HELPERS ───────────────────────────────────────────────

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  getCurrentUserId(): number | null {
    return this.currentUser()?.userId || null;
  }

  getCurrentUsername(): string | null {
    return this.currentUser()?.username || null;
  }

  getCurrentRole(): string | null {
    return this.currentUser()?.role || null;
  }

  private getStoredToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private getStoredUser(): AuthResponse | null {
    const user = localStorage.getItem(this.USER_KEY);
    return user ? JSON.parse(user) : null;
  }

  private fetchProfileAndStore(auth: AuthResponse, username: string): void {
    // We call the user service directly to avoid circular dependency
    this.http.get<any>(`/api/users/username/${username}`).subscribe({
      next: user => {
        const combined = {
          ...auth,
          ...user,
          userId: user.userId || user.id,
          role: user.role || auth.role // Ensure role from profile is used if it exists
        };
        this.storeAuth(combined);
      },
      error: () => this.storeAuth(auth)
    });
  }

  storeAuth(data: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, data.accessToken);
    if (data.refreshToken) {
      localStorage.setItem(this.REFRESH_KEY, data.refreshToken);
    }
    localStorage.setItem(this.USER_KEY, JSON.stringify(data));
    this.currentUser.set(data);
    this.isLoggedIn.set(true);
  }
}
