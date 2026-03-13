import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { AuthResponse } from '../../../shared/models/models';

@Component({
    selector: 'app-oauth-callback',
    standalone: true,
    template: `
    <div class="callback-container">
      <div class="loader"> Authenticating with Google... </div>
    </div>
  `,
    styles: [`
    .callback-container {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      font-family: var(--font-family);
      background: var(--bg-primary);
      color: var(--text-primary);
    }
    .loader {
      font-size: 1.2rem;
      font-weight: 500;
    }
  `]
})
export class OauthCallbackComponent implements OnInit {
    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private authService: AuthService
    ) { }

    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            const token = params['token'];
            if (token) {
                const auth: AuthResponse = {
                    accessToken: token,
                    refreshToken: params['refreshToken'] || '',
                    tokenType: 'Bearer',
                    userId: Number(params['userId']),
                    username: params['username'],
                    email: params['email'],
                    displayName: params['displayName'],
                    profilePicture: params['profilePicture'],
                    role: params['role'] as any
                };

                this.authService.storeAuth(auth);
                this.router.navigate(['/feed']);
            } else {
                console.error('No token found in OAuth callback');
                this.router.navigate(['/auth/login'], { queryParams: { error: 'oauth_failed' } });
            }
        });
    }
}
