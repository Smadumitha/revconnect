import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { NotificationService } from '../../core/services/notification.service';
import { AuthService } from '../../core/services/auth.service';
import { AnalyticsService, UserAnalytics } from '../../core/services/analytics.service';
import { Notification, NotificationPreferences } from '../../shared/models/models';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent, RouterLink],
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class NotificationsComponent implements OnInit {
  notifications = signal<Notification[]>([]);
  loading = signal(true);
  loadingMore = signal(false);
  markingAll = signal(false);
  showPrefs = signal(false);
  savingPrefs = signal(false);
  prefs = signal<NotificationPreferences | null>(null);
  filter = signal<'all' | 'unread' | 'connections' | 'posts'>('all');
  currentPage = 0;
  hasMore = signal(false);
  engagement = signal<any>(null);

  prefOptions = [
    { key: 'connectionRequests' as keyof NotificationPreferences, label: 'Connection Requests', desc: 'When someone sends you a connection request', icon: 'fas fa-user-plus' },
    { key: 'postLikes' as keyof NotificationPreferences, label: 'Post Likes', desc: 'When someone likes your posts', icon: 'fas fa-heart' },
    { key: 'postComments' as keyof NotificationPreferences, label: 'Comments', desc: 'When someone comments on your posts', icon: 'fas fa-comment' },
    { key: 'postShares' as keyof NotificationPreferences, label: 'Shares & Reposts', desc: 'When someone shares your content', icon: 'fas fa-retweet' },
    { key: 'newFollowers' as keyof NotificationPreferences, label: 'New Followers', desc: 'When someone follows you', icon: 'fas fa-user-friends' },
  ];

  constructor(
    private notifService: NotificationService,
    public authService: AuthService,
    private analyticsService: AnalyticsService
  ) { }

  ngOnInit(): void {
    this.loadNotifications();
    this.loadPrefs();
    this.loadAnalytics();
  }

  loadAnalytics(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    this.analyticsService.getUserAnalytics(userId).subscribe((res: UserAnalytics | null) => {
      if (res) this.engagement.set(res);
    });
  }

  loadNotifications(page = 0): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    if (page === 0) this.loading.set(true); else this.loadingMore.set(true);
    this.notifService.getNotifications(userId, page, 20).subscribe({
      next: res => {
        const items = res.content || [];
        if (page === 0) this.notifications.set(items);
        else this.notifications.update(n => [...n, ...items]);
        this.hasMore.set(!res.last);
        this.currentPage = page;
        this.loading.set(false);
        this.loadingMore.set(false);
      },
      error: () => { this.loading.set(false); this.loadingMore.set(false); }
    });
  }

  loadMore(): void { this.loadNotifications(this.currentPage + 1); }

  loadPrefs(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    this.notifService.getPreferences(userId).subscribe({
      next: p => this.prefs.set(p),
      error: () => this.prefs.set({ connectionRequests: true, postLikes: true, postComments: true, postShares: true, newFollowers: true })
    });
  }

  getPref(key: keyof NotificationPreferences): boolean {
    return this.prefs()?.[key] ?? true;
  }

  togglePref(key: keyof NotificationPreferences): void {
    const p = this.prefs();
    if (!p) return;
    this.prefs.set({ ...p, [key]: !p[key] });
  }

  savePrefs(): void {
    const userId = this.authService.getCurrentUserId();
    const p = this.prefs();
    if (!userId || !p) return;
    this.savingPrefs.set(true);
    this.notifService.updatePreferences(userId, p).subscribe({
      next: updated => { this.prefs.set(updated); this.savingPrefs.set(false); this.showPrefs.set(false); },
      error: () => this.savingPrefs.set(false)
    });
  }

  markRead(n: Notification): void {
    if (n.read) return;
    this.notifService.markAsRead(n.id).subscribe(() => {
      this.notifications.update(list => list.map(x => x.id === n.id ? { ...x, read: true } : x));
      this.notifService.unreadCount.update(c => Math.max(0, c - 1));
    });
  }

  markAllRead(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;
    this.markingAll.set(true);
    this.notifService.markAllAsRead(userId).subscribe({
      next: () => {
        this.notifications.update(list => list.map(n => ({ ...n, read: true })));
        this.notifService.unreadCount.set(0);
        this.markingAll.set(false);
      },
      error: () => this.markingAll.set(false)
    });
  }

  getFiltered(): Notification[] {
    const all = this.notifications();
    const f = this.filter();
    if (f === 'unread') return all.filter(n => !n.read);
    if (f === 'connections') return all.filter(n => ['CONNECTION_REQUEST', 'CONNECTION_ACCEPTED', 'NEW_FOLLOWER'].includes(n.type));
    if (f === 'posts') return all.filter(n => ['POST_LIKED', 'POST_COMMENTED', 'POST_SHARED', 'POST_REPOSTED'].includes(n.type));
    return all;
  }

  getUnreadCount(): number { return this.notifications().filter(n => !n.read).length; }
  getCountByType(type: string): number { return this.notifications().filter(n => n.type === type).length; }

  getIcon(type: string): string {
    const m: Record<string, string> = {
      POST_LIKED: 'fas fa-heart', POST_COMMENTED: 'fas fa-comment',
      POST_SHARED: 'fas fa-retweet', POST_REPOSTED: 'fas fa-retweet',
      CONNECTION_REQUEST: 'fas fa-user-plus', CONNECTION_ACCEPTED: 'fas fa-handshake', NEW_FOLLOWER: 'fas fa-user-friends'
    };
    return m[type] || 'fas fa-bell';
  }

  getIconClass(type: string): string {
    if (type === 'POST_LIKED') return 'icon-like';
    if (type === 'POST_COMMENTED') return 'icon-comment';
    if (type === 'POST_SHARED' || type === 'POST_REPOSTED') return 'icon-share';
    if (type === 'CONNECTION_REQUEST' || type === 'CONNECTION_ACCEPTED') return 'icon-connection';
    if (type === 'NEW_FOLLOWER') return 'icon-follower';
    return 'icon-default';
  }

  getBadgeClass(type: string): string {
    if (type === 'POST_LIKED') return 'like';
    if (type === 'POST_COMMENTED') return 'comment';
    if (type === 'POST_SHARED' || type === 'POST_REPOSTED') return 'share';
    if (type === 'CONNECTION_REQUEST' || type === 'CONNECTION_ACCEPTED') return 'connection';
    if (type === 'NEW_FOLLOWER') return 'follower';
    return 'share';
  }

  getTypeLabel(type: string): string {
    const m: Record<string, string> = {
      POST_LIKED: 'Like', POST_COMMENTED: 'Comment', POST_SHARED: 'Share',
      POST_REPOSTED: 'Repost', CONNECTION_REQUEST: 'Request', CONNECTION_ACCEPTED: 'Connected', NEW_FOLLOWER: 'Follower'
    };
    return m[type] || 'Alert';
  }

  timeAgo(dateStr: string): string {
    const date = new Date(dateStr);
    const diff = Math.floor((Date.now() - date.getTime()) / 1000);
    if (diff < 60) return 'just now';
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h ago`;
    return `${Math.floor(diff / 86400)}d ago`;
  }

  trackNotif(_: number, n: Notification): number { return n.id; }
}
