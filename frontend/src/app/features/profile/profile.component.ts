import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { PostCardComponent } from '../../shared/components/post-card/post-card.component';
import { UserService } from '../../core/services/user.service';
import { PostService } from '../../core/services/post.service';
import { AuthService } from '../../core/services/auth.service';
import { ConnectionService } from '../../core/services/connection.service';
import { AnalyticsService } from '../../core/services/analytics.service';
import { Post, User } from '../../shared/models/models';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, PostCardComponent, RouterLink],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  user = signal<User | null>(null);
  posts = signal<Post[]>([]);
  loading = signal(true);
  postsLoading = signal(true);

  // New connection state
  connectionState = signal({
    isFollowing: false,
    isConnected: false,
    isPendingSent: false,
    isPendingReceived: false
  });

  constructor(
    private route: ActivatedRoute,
    private userService: UserService,
    private postService: PostService,
    public authService: AuthService,
    private connectionService: ConnectionService,
    private analyticsService: AnalyticsService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.resetPage();
      const username = params['username'];
      this.loadUser(username);
    });
  }

  private resetPage() {
    this.loading.set(true);
    this.postsLoading.set(true);
    this.posts.set([]);
  }

  private loadUser(username: string) {
    this.userService.getUserByUsername(username).subscribe({
      next: user => {
        this.user.set(user);
        this.loading.set(false);
        const targetId = user.userId || user.id;
        const myId = this.authService.getCurrentUserId();

        if (myId && targetId !== myId) {
          this.checkConnectionStatus(myId, targetId);
          this.analyticsService.trackProfileView(targetId).subscribe();
        }

        this.loadPosts(targetId);
      },
      error: () => { this.loading.set(false); this.router.navigate(['/feed']); }
    });
  }

  private checkConnectionStatus(myId: number, targetId: number) {
    this.connectionService.getConnectionStatus(myId, targetId).subscribe(status => {
      this.connectionState.set(status);
      // Sync isFollowing to user object for existing template logic if needed
      this.user.update(u => u ? { ...u, isFollowing: status.isFollowing } : u);
    });
  }

  loadPosts(userId: number): void {
    this.postService.getUserPosts(userId).subscribe(res => {
      this.posts.set(res.content || []);
      this.postsLoading.set(false);
    });
  }

  isOwn(): boolean {
    const myId = this.authService.getCurrentUserId();
    const u = this.user();
    return !!(u && myId && ((u.id === myId) || (u.userId === myId)));
  }

  isPrivateAccount(): boolean {
    const u = this.user();
    return !!(u && (u.isPrivate || u.privacy === 'PRIVATE'));
  }

  isCreatorOrBusiness(): boolean {
    const role = this.user()?.role;
    return role === 'CREATOR' || role === 'BUSINESS';
  }


  sendConnection(): void {
    if (this.connectionState().isPendingSent || this.connectionState().isConnected) return;
    const u = this.user();
    const myId = this.authService.getCurrentUserId();
    if (!u || !myId) return;

    this.connectionService.sendRequest(myId, u.userId || u.id).subscribe({
      next: () => this.connectionState.update(s => ({ ...s, isPendingSent: true })),
      error: () => { }
    });
  }

  onPostDeleted(id: number): void {
    this.posts.update(p => p.filter(x => x.id !== id));
  }

  getInitials(): string {
    const name = this.user()?.displayName || '';
    return name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() || '?';
  }

  getRoleLabel(role: string): string {
    const labels: Record<string, string> = { PERSONAL: '👤 Personal', CREATOR: '⭐ Creator', BUSINESS: '🏢 Business' };
    return labels[role] || role;
  }

  trackPost(_: number, post: Post): number { return post.id; }

  getProfilePictureUrl(): string | null {
    const url = this.user()?.profilePicture;
    if (!url) return null;
    if (url.startsWith('http') || url.startsWith('data:')) return url;
    // If it's a relative path like /uploads/..., extract the filename and point to the API
    const filename = url.split('/').pop();
    return filename ? `/api/users/media/${filename}` : url;
  }
}
