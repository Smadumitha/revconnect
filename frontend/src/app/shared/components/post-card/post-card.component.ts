import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { Post, Comment } from '../../models/models';
import { PostService } from '../../../core/services/post.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-post-card',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './post-card.component.html',
  styleUrls: ['./post-card.component.css']
})
export class PostCardComponent {
  @Input() post!: Post;
  @Output() deleted = new EventEmitter<number>();
  @Output() hashtagClicked = new EventEmitter<string>();

  showComments = signal(false);
  comments = signal<Comment[]>([]);
  commentsLoading = signal(false);
  commentText = '';
  showAnalytics = signal(false);
  analyticsLoading = signal(false);
  menuOpen = signal(false);
  editMode = signal(false);
  editContent = '';
  saving = signal(false);
  showRepostModal = signal(false);
  repostComment = '';
  reposting = signal(false);

  engagementHistory = [20, 45, 30, 60, 50, 75, 40, 80, 55, 65, 35, 70];

  constructor(
    private postService: PostService,
    public authService: AuthService,
    private router: Router,
    private sanitizer: DomSanitizer
  ) { }

  isOwner(): boolean {
    return this.post.userId === this.authService.getCurrentUserId();
  }

  // ── DELETE POST ──────────────────────────────────────────────
  deletePost(): void {
    if (!confirm('Delete this post?')) return;
    this.menuOpen.set(false);
    this.postService.deletePost(this.post.id).subscribe(() => {
      this.deleted.emit(this.post.id);
    });
  }

  // ── EDIT POST ───────────────────────────────────────────────
  startEdit(): void {
    this.editContent = this.post.content;
    this.editMode.set(true);
    this.menuOpen.set(false);
  }

  cancelEdit(): void { this.editMode.set(false); }

  saveEdit(): void {
    if (!this.editContent.trim()) return;
    this.saving.set(true);
    this.postService.updatePost(this.post.id, { content: this.editContent }).subscribe({
      next: updated => {
        this.post = { ...this.post, content: updated.content };
        this.editMode.set(false);
        this.saving.set(false);
      },
      error: () => this.saving.set(false)
    });
  }

  // ── PIN POST ────────────────────────────────────────────────
  pinPost(): void {
    this.menuOpen.set(false);
    this.postService.updatePost(this.post.id, { pinned: !this.post.pinned }).subscribe(updated => {
      this.post = { ...this.post, pinned: updated.pinned };
    });
  }

  // ── ANALYTICS ───────────────────────────────────────────────
  toggleAnalytics(): void {
    this.showAnalytics.update(v => !v);
    if (this.showAnalytics()) {
      this.analyticsLoading.set(true);
      setTimeout(() => this.analyticsLoading.set(false), 600);
    }
  }

  getEngagementRate(): string {
    const total = (this.post.likesCount || 0) + (this.post.commentsCount || 0) + (this.post.sharesCount || 0);
    return (total > 0 ? Math.min((total / 100) * 4.2, 9.9) : 0).toFixed(1);
  }

  // ── LIKE ─────────────────────────────────────────────────────
  savingLike = false;
  onLike(): void {
    if (this.savingLike) return;
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.savingLike = true;
    const wasLiked = this.post.isLiked;
    const initialCount = this.post.likesCount || 0;

    if (wasLiked) {
      // Optimistic Unlike
      this.post = { ...this.post, isLiked: false, likesCount: Math.max(0, initialCount - 1) };
      this.postService.unlikePost(userId, this.post.id).subscribe({
        next: () => { this.savingLike = false; },
        error: () => {
          // Revert
          this.post = { ...this.post, isLiked: true, likesCount: initialCount };
          this.savingLike = false;
        }
      });
    } else {
      // Optimistic Like
      this.post = { ...this.post, isLiked: true, likesCount: initialCount + 1 };
      this.postService.likePost(userId, this.post.id).subscribe({
        next: () => { this.savingLike = false; },
        error: (err) => {
          if (err.status !== 409) {
            // Revert
            this.post = { ...this.post, isLiked: false, likesCount: initialCount };
          }
          this.savingLike = false;
        }
      });
    }
  }

  // ── SHARE ────────────────────────────────────────────────────
  onShare(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    if (this.post.isShared) {
      this.postService.undoShare(userId, this.post.id).subscribe(() => {
        this.post = { ...this.post, sharesCount: Math.max(0, (this.post.sharesCount || 0) - 1), isShared: false };
      });
    } else {
      this.postService.sharePost(userId, this.post.id).subscribe({
        next: () => {
          this.post = { ...this.post, sharesCount: (this.post.sharesCount || 0) + 1, isShared: true };
        },
        error: (err) => {
          // 409 = already shared — treat as success, just mark as shared
          if (err.status === 409) {
            this.post = { ...this.post, isShared: true };
          }
        }
      });
    }
  }

  // ── REPOST ───────────────────────────────────────────────────
  onRepost(): void {
    this.showRepostModal.set(true);
    this.repostComment = '';
  }

  submitRepost(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    this.reposting.set(true);
    this.postService.repost(userId, this.post.id, this.repostComment).subscribe({
      next: () => {
        this.showRepostModal.set(false);
        this.reposting.set(false);
        this.repostComment = '';
      },
      error: () => this.reposting.set(false)
    });
  }

  // ── COMMENTS ─────────────────────────────────────────────────
  toggleComments(): void {
    this.showComments.update(v => !v);
    if (this.showComments() && this.comments().length === 0) {
      this.commentsLoading.set(true);
      this.postService.getComments(this.post.id).subscribe(res => {
        const data = res.data;
        this.comments.set(Array.isArray(data) ? data : []);
        this.commentsLoading.set(false);
      });
    }
  }

  submitComment(): void {
    if (!this.commentText.trim()) return;
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    const text = this.commentText.trim();
    this.postService.addComment(userId, this.post.id, text).subscribe(() => {
      const newComment: Comment = {
        id: Date.now(), content: text, userId,
        postId: this.post.id, createdAt: new Date().toISOString()
      };
      this.comments.update(c => [newComment, ...c]);
      this.post = { ...this.post, commentsCount: this.post.commentsCount + 1 };
      this.commentText = '';
    });
  }

  deleteComment(commentId: number): void {
    this.postService.deleteComment(commentId).subscribe(() => {
      this.comments.update(c => c.filter(x => x.id !== commentId));
      this.post = { ...this.post, commentsCount: Math.max(0, this.post.commentsCount - 1) };
    });
  }

  // ── HASHTAG CLICK ─────────────────────────────────────────────
  onHashtagClick(tag: string): void {
    this.hashtagClicked.emit(tag);
    this.router.navigate(['/search'], { queryParams: { q: '#' + tag, filter: 'posts' } });
  }

  // ── HELPERS ───────────────────────────────────────────────────
  getInitials(name: string): string {
    if (!name) return '??';
    return name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
  }

  getCurrentUserInitials(): string {
    const name = this.authService.currentUser()?.displayName || '';
    return name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase() || '?';
  }

  getColor(seed: string): string {
    const colors = ['#6c63ff, #8b85ff', '#ff6584, #ff8fa3', '#4ecb71, #43b8b8', '#f5a623, #f78c2c', '#8b5cf6, #6366f1'];
    let hash = 0;
    for (let i = 0; i < seed.length; i++) hash = seed.charCodeAt(i) + ((hash << 5) - hash);
    return colors[Math.abs(hash) % colors.length];
  }

  getRoleIcon(role: string): string {
    const icons: Record<string, string> = { PERSONAL: '👤', CREATOR: '⭐', BUSINESS: '🏢' };
    return icons[role] || '';
  }

  safeContent(text: string): SafeHtml {
    if (!text) return '';
    const html = text
      .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
      .replace(/#(\w+)/g, '<span class="hashtag-text" style="color:#8b85ff;cursor:pointer">#$1</span>')
      .replace(/@(\w+)/g, '<span style="color:#6c63ff">@$1</span>');
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }

  formatContent(text: string): string {
    return text || '';
  }

  openUrl(url: string, event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    if (url.startsWith('http')) {
      window.open(url, '_blank');
    } else {
      window.open('https://' + url, '_blank');
    }
  }

  timeAgo(dateStr: string): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const diff = Math.floor((Date.now() - date.getTime()) / 1000);
    if (diff < 60) return 'just now';
    if (diff < 3600) return `${Math.floor(diff / 60)}m`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h`;
    if (diff < 604800) return `${Math.floor(diff / 86400)}d`;
    return date.toLocaleDateString();
  }
}
