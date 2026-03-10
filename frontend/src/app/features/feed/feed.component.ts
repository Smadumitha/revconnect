import { Component, OnInit, signal } from '@angular/core';
import { map } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { PostCardComponent } from '../../shared/components/post-card/post-card.component';
import { PostService } from '../../core/services/post.service';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { Post, User } from '../../shared/models/models';
import { RouterLink, Router } from '@angular/router';

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, PostCardComponent, RouterLink],
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css']
})
export class FeedComponent implements OnInit {
  posts = signal<Post[]>([]);
  loading = signal(true);
  creating = signal(false);
  selectedFile: File | null = null;
  imagePreview: string | null = null;
  showCreateForm = signal(false);
  activeTab = signal<'feed' | 'trending' | 'promotional' | 'scheduled'>('feed');
  hasMore = signal(false);
  currentPage = 0;
  currentUser = signal<User | null>(null);

  newPost: any = {
    content: '',
    type: 'TEXT',
    hashtags: '',
    productTags: '',
    ctaText: '',
    ctaUrl: '',
    scheduledAt: '',
    pinned: false
  };

  trendingTags: string[] = [];

  constructor(
    private postService: PostService,
    public authService: AuthService,
    private userService: UserService,
    private router: Router
  ) { }


  loadTrendingTags(): void {
    this.postService.getTrendingTags().subscribe(tags => {
      this.trendingTags = tags.length > 0 ? tags : ['RevConnect', 'Design', 'Microservices', 'Tech2026', 'Innovation'];
    });
  }

  loadCurrentUser(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    this.userService.getMe(userId).subscribe(user => this.currentUser.set(user));
  }

  ngOnInit(): void {
    this.loadFeed();
    this.loadCurrentUser();
    this.loadTrendingTags();
  }

  loadFeed(): void {
    this.loading.set(true);
    const userId = this.authService.getCurrentUserId();
    if (!userId && this.activeTab() === 'feed') {
      this.loading.set(false);
      return;
    }

    let obs;
    switch (this.activeTab()) {
      case 'promotional':
        obs = this.postService.getPromotionalPosts();
        break;
      case 'scheduled':
        obs = this.postService.getUserPosts(userId!).pipe(
          map(posts => posts.filter(p => p.status === 'SCHEDULED'))
        );
        break;
      case 'trending':
        obs = this.postService.getTrending();
        break;
      default:
        obs = this.postService.getHomeFeed(userId!);
    }

    obs.subscribe({
      next: posts => {
        if (this.currentPage === 0) {
          this.posts.set(posts);
        } else {
          this.posts.update(p => [...p, ...posts]);
        }
        // Backend doesn't support pagination properly yet, so hasMore is false
        this.hasMore.set(false);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  switchTab(tab: 'feed' | 'trending' | 'promotional' | 'scheduled'): void {
    this.activeTab.set(tab);
    this.currentPage = 0;
    this.loadFeed();
  }

  loadMore(): void {
    this.currentPage++;
    this.loadFeed();
  }

  createPost(): void {
    if (!this.newPost.content.trim()) return;
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.creating.set(true);

    // ✅ NEW: Process hashtags and product tags into arrays
    const hashtagArray = this.newPost.hashtags ? this.newPost.hashtags.split(',').map((t: string) => t.trim().replace('#', '')) : [];
    const productTagArray = this.newPost.productTags ? this.newPost.productTags.split(',').map((t: string) => t.trim()) : [];

    this.postService.createPost({
      userId: userId,
      content: this.newPost.content,
      type: this.selectedFile ? 'IMAGE' : this.newPost.type,
      hashtags: hashtagArray,
      productTags: productTagArray,
      promotional: this.newPost.type === 'PROMOTIONAL',
      ctaText: this.newPost.ctaText || undefined,
      ctaUrl: this.newPost.ctaUrl || undefined,
      scheduledAt: this.newPost.scheduledAt || undefined,
      pinned: this.newPost.pinned
    }).subscribe({
      next: post => {
        // Ensure author is populated for immediate UI feedback
        if (!post.author) {
          const cu = this.authService.currentUser();
          post.author = {
            id: userId,
            userId: userId,
            displayName: cu?.displayName || 'Me',
            username: cu?.username || '',
            profilePicture: cu?.profilePicture,
            role: cu?.role || 'PERSONAL',
            followersCount: 0,
            followingCount: 0
          } as User;
        }

        if (this.selectedFile) {
          this.postService.uploadMedia(post.id, this.selectedFile).subscribe({
            next: (url) => {
              post.mediaUrl = url;
              this.finalizePostCreation(post);
            },
            error: () => this.finalizePostCreation(post)
          });
        } else {
          this.finalizePostCreation(post);
        }
      },
      error: () => this.creating.set(false)
    });
  }

  finalizePostCreation(post: Post): void {
    if (!this.newPost.scheduledAt) {
      this.posts.update(p => [post, ...p]);
    }
    this.newPost = {
      content: '', type: 'TEXT', hashtags: '', productTags: '',
      ctaText: '', ctaUrl: '', scheduledAt: '', pinned: false
    };
    this.selectedFile = null;
    this.imagePreview = null;
    this.showCreateForm.set(false);
    this.creating.set(false);
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = () => this.imagePreview = reader.result as string;
      reader.readAsDataURL(file);
    }
  }

  removeImage(): void {
    this.selectedFile = null;
    this.imagePreview = null;
  }

  onPostDeleted(id: number): void {
    this.posts.update(p => p.filter(x => x.id !== id));
  }

  onHashtagClick(tag: string): void {
    this.router.navigate(['/search'], { queryParams: { q: '#' + tag, filter: 'posts' } });
  }

  getInitials(): string {
    const name = this.authService.currentUser()?.displayName || '';
    return name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
  }

  getInitials2(name: string): string {
    return (name || '').split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
  }

  isCreatorOrBusiness(): boolean {
    const role = this.authService.currentUser()?.role;
    return role === 'CREATOR' || role === 'BUSINESS';
  }

  trackPost(_: number, post: Post): number { return post.id; }
}
