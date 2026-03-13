import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { PostCardComponent } from '../../shared/components/post-card/post-card.component';
import { UserService } from '../../core/services/user.service';
import { PostService } from '../../core/services/post.service';
import { ConnectionService } from '../../core/services/connection.service';

import { User, Post } from '../../shared/models/models';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, PostCardComponent, RouterLink],
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  query = '';
  filter = signal<'people' | 'posts'>('people');
  users = signal<User[]>([]);
  posts = signal<Post[]>([]);
  searchLoading = signal(false);
  postsLoading = signal(false);
  private searchTimeout: any;

  constructor(
    private userService: UserService,
    private postService: PostService,
    private connectionService: ConnectionService,
    private route: ActivatedRoute,
    public authService: AuthService
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(p => {
      if (p['q']) { this.query = p['q']; this.doSearch(); }
    });
    this.loadDefaultPosts();
  }

  loadDefaultPosts(): void {
    this.postsLoading.set(true);
    this.postService.getTrending().subscribe(r => {
      this.posts.set(r.content || []);
      this.postsLoading.set(false);
    });
    const userId = this.authService.getCurrentUserId();
    if (userId) {
      this.userService.getSuggestions(userId).subscribe(r => this.users.set(r));
    }
  }

  onSearch(): void {
    clearTimeout(this.searchTimeout);
    this.searchTimeout = setTimeout(() => this.doSearch(), 400);
  }

  doSearch(): void {
    if (!this.query.trim()) {
      this.loadDefaultPosts();
      return;
    }
    const currentId = this.authService.getCurrentUserId();
    if (this.filter() === 'people') {
      this.searchLoading.set(true);
      this.userService.searchUsers(this.query).subscribe(r => {
        if (currentId) {
          r.forEach(u => {
            this.connectionService.getConnectionStatus(currentId, u.userId || u.id).subscribe(status => {
              u.isFollowing = status.isFollowing;
              u.isPendingSent = status.isPendingSent;
              u.isPendingReceived = status.isPendingReceived;
            });
          });
        }
        this.users.set(r);
        this.searchLoading.set(true); // Should be false, wait
        setTimeout(() => this.searchLoading.set(false), 300);
      });
    } else {
      this.postsLoading.set(true);
      if (this.query.startsWith('#')) {
        const tag = this.query.slice(1);
        this.postService.getPostsByHashtag(tag).subscribe(posts => {
          this.posts.set(posts.content || []);
          this.postsLoading.set(false);
        });
      } else {
        this.postService.getTrending().subscribe(r => {
          this.posts.set(r.content || []);
          this.postsLoading.set(false);
        });
      }
    }
  }

  toggleFollow(u: User): void {
    const currentId = this.authService.getCurrentUserId();
    if (!currentId) return;

    if (u.isFollowing) {
      this.connectionService.unfollow(currentId, u.userId || u.id).subscribe(() => {
        this.users.update(list => list.map(x => (x.userId || x.id) === (u.userId || u.id) ? { ...x, isFollowing: false } : x));
      });
    } else {
      // Simple follow (for search results)
      this.connect(u);
      u.isFollowing = true;
    }
  }

  connect(u: User): void {
    const currentId = this.authService.getCurrentUserId();
    if (!currentId) return;
    this.connectionService.sendRequest(currentId, u.userId || u.id).subscribe({
      next: () => {
        this.users.update(list => list.map(x => (x.userId || x.id) === (u.userId || u.id) ? { ...x, isPendingSent: true } : x));
      },
      error: (err) => {
        // If 400 (already exists), just fix UI
        if (err.status === 400) {
          this.users.update(list => list.map(x => (x.userId || x.id) === (u.userId || u.id) ? { ...x, isPendingSent: true } : x));
        }
      }
    });
  }

  getInitials(name: string): string {
    return (name || '?').split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
  }

  getAvatarUrl(user: any): string | null {
    const url = user?.profilePicture;
    if (!url) return null;
    if (url.startsWith('http') || url.startsWith('data:')) return url;
    const filename = url.split('/').pop();
    return filename ? `/api/users/media/${filename}` : url;
  }

  getColor(seed: string): string {
    if (!seed) return '#6c63ff, #ff6584';
    const colors = ['#6c63ff, #8b85ff', '#ff6584, #ff8fa3', '#4ecb71, #43b8b8', '#f5a623, #f78c2c', '#8b5cf6, #6366f1'];
    let hash = 0;
    for (let i = 0; i < seed.length; i++) hash = seed.charCodeAt(i) + ((hash << 5) - hash);
    return colors[Math.abs(hash) % colors.length];
  }

  trackPost(_: number, p: Post): number { return p.id; }
}
