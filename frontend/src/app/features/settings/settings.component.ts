import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { User } from '../../shared/models/models';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, RouterLink],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {
  user = signal<User | null>(null);
  loading = signal(true);
  saving = signal(false);
  savingPrivacy = signal(false);
  deleting = signal(false);
  success = signal('');
  error = signal('');
  section = signal<'profile' | 'privacy' | 'account' | 'business'>('profile');
  showDeleteModal = signal(false);
  deleteConfirmText = '';

  form: any = {
    displayName: '', bio: '', location: '', website: '',
    isPrivate: false, industry: '', category: '',
    businessAddress: '', businessHours: '', contactEmail: ''
  };

  constructor(
    private userService: UserService,
    public authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) { this.router.navigate(['/auth/login']); return; }
    this.userService.getMe(userId).subscribe({
      next: u => {
        this.user.set(u);
        this.resetForm();
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  resetForm(): void {
    const u = this.user();
    if (!u) return;
    this.form = {
      displayName: u.displayName || '',
      bio: u.bio || '',
      location: u.location || '',
      website: u.website || '',
      isPrivate: u.isPrivate || u.privacy === 'PRIVATE' || false,
      industry: u.industry || '',
      category: u.category || '',
      businessAddress: u.businessAddress || '',
      businessHours: u.businessHours || '',
      contactEmail: u.contactEmail || '',
    };
  }

  saveProfile(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    this.saving.set(true);
    this.success.set('');
    this.error.set('');
    this.userService.updateProfile(userId, this.form).subscribe({
      next: updated => {
        this.user.set(updated);
        this.saving.set(false);
        this.success.set('Profile updated successfully!');
        setTimeout(() => this.success.set(''), 3000);
      },
      error: err => {
        this.saving.set(false);
        this.error.set(err.error?.message || 'Failed to update profile');
      }
    });
  }

  savePrivacy(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    this.savingPrivacy.set(true);
    // Use updateProfile with isPrivate field (privacy endpoint may not exist)
    this.userService.updateProfile(userId, { isPrivate: this.form.isPrivate }).subscribe({
      next: (updated) => {
        this.user.update(u => u ? { ...u, isPrivate: this.form.isPrivate } : u);
        this.savingPrivacy.set(false);
        this.success.set('Privacy settings saved!');
        setTimeout(() => this.success.set(''), 3000);
      },
      error: () => {
        // Try the dedicated privacy endpoint as fallback
        this.userService.updatePrivacy(userId, this.form.isPrivate).subscribe({
          next: () => {
            this.savingPrivacy.set(false);
            this.success.set('Privacy settings saved!');
            setTimeout(() => this.success.set(''), 3000);
          },
          error: (err) => {
            this.savingPrivacy.set(false);
            this.error.set(err.error?.message || 'Failed to update privacy settings');
          }
        });
      }
    });
  }

  togglePrivacy(): void {
    this.form.isPrivate = !this.form.isPrivate;
  }

  onAvatarChange(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (!file) return;
    // Preview locally immediately
    const reader = new FileReader();
    reader.onload = (e) => {
      const dataUrl = e.target?.result as string;
      this.user.update(u => u ? { ...u, profilePicture: dataUrl } : u);
    };
    reader.readAsDataURL(file);
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    this.userService.uploadProfilePicture(userId, file).subscribe({
      next: res => {
        this.user.update(u => u ? { ...u, profilePicture: res.url } : u);
        this.success.set('Profile picture updated!');
        setTimeout(() => this.success.set(''), 3000);
      },
      error: () => {
        // Local preview already shown — inform user backend upload unavailable
        this.success.set('Preview updated locally. Backend image upload may not be available yet.');
        setTimeout(() => this.success.set(''), 5000);
      }
    });
  }

  confirmDelete(): void {
    this.deleteConfirmText = '';
    this.showDeleteModal.set(true);
  }

  deleteAccount(): void {
    if (this.deleteConfirmText !== 'DELETE') return;
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    this.deleting.set(true);
    this.userService.deleteProfile(userId).subscribe({
      next: () => {
        this.authService.logout();
      },
      error: err => {
        this.deleting.set(false);
        this.error.set(err.error?.message || 'Failed to delete account');
        this.showDeleteModal.set(false);
      }
    });
  }

  signOutAll(): void {
    this.authService.logout();
  }

  isCreatorOrBusiness(): boolean {
    const role = this.user()?.role || this.authService.currentUser()?.role;
    return role === 'CREATOR' || role === 'BUSINESS';
  }

  getInitials(): string {
    const name = this.user()?.displayName || '';
    return name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() || '?';
  }

  formatJoinDate(): string {
    const d = this.user()?.createdAt;
    if (!d) return 'N/A';
    return new Date(d).toLocaleDateString('en-US', { year: 'numeric', month: 'long' });
  }

  getAvatarUrl(): string | null {
    const url = this.user()?.profilePicture;
    if (!url) return null;
    if (url.startsWith('http') || url.startsWith('data:')) return url;
    const filename = url.split('/').pop();
    return filename ? `/api/users/media/${filename}` : url;
  }
}
