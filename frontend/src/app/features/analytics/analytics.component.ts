import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { PostService } from '../../core/services/post.service';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { AnalyticsService, UserAnalytics } from '../../core/services/analytics.service';
import { Post, User } from '../../shared/models/models';

interface DayData { label: string; likes: number; comments: number; shares: number; date: Date; }

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule, NavbarComponent, RouterLink],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.css']
})
export class AnalyticsComponent implements OnInit {
  posts = signal<Post[]>([]);
  currentUser = signal<User | null>(null);
  metrics = signal<UserAnalytics | null>(null);
  loading = signal(true);
  period = signal<'7d' | '30d' | 'all'>('30d');
  chartData = signal<DayData[]>([]);

  svgW = 600;
  svgH = 160;
  readonly padding = { top: 10, right: 10, bottom: 5, left: 5 };

  constructor(
    private postService: PostService,
    private userService: UserService,
    public authService: AuthService,
    private analyticsService: AnalyticsService
  ) { }

  ngOnInit(): void {
    if (!this.hasAccess()) return;
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.userService.getMe(userId).subscribe(u => {
      this.currentUser.set(u);
      this.loadMetrics(userId, u.followersCount);
    });

    this.postService.getUserPosts(userId).subscribe({
      next: p => {
        this.posts.set(Array.isArray(p) ? p : []);
        this.buildChart();
        this.loading.set(false);
      },
      error: () => { this.loading.set(false); }
    });
  }

  loadMetrics(userId: number, followers: number): void {
    this.analyticsService.getUserAnalytics(userId, followers).subscribe(m => this.metrics.set(m));
  }

  hasAccess(): boolean {
    const role = (this.authService.currentUser()?.role || '').toUpperCase();
    return role === 'CREATOR' || role === 'BUSINESS';
  }

  isBusiness(): boolean {
    return (this.authService.currentUser()?.role || '').toUpperCase() === 'BUSINESS';
  }

  buildChart(): void {
    const ps = this.posts();
    const days = this.period() === '7d' ? 7 : this.period() === '30d' ? 14 : 30;
    const result: DayData[] = [];

    const byDate = new Map<string, { likes: number; comments: number; shares: number }>();
    for (let i = 0; i < days; i++) {
      const d = new Date();
      d.setDate(d.getDate() - (days - 1 - i));
      byDate.set(d.toDateString(), { likes: 0, comments: 0, shares: 0 });
    }

    ps.forEach(p => {
      const key = new Date(p.createdAt).toDateString();
      if (byDate.has(key)) {
        const cur = byDate.get(key)!;
        byDate.set(key, {
          likes: cur.likes + (p.likesCount || 0),
          comments: cur.comments + (p.commentsCount || 0),
          shares: cur.shares + (p.sharesCount || 0),
        });
      }
    });

    let idx = 0;
    byDate.forEach((val, _) => {
      const d = new Date();
      d.setDate(d.getDate() - (days - 1 - idx));
      result.push({ label: '', ...val, date: d });
      idx++;
    });

    this.chartData.set(result);
  }

  get chartMax(): number {
    const d = this.chartData();
    return Math.max(...d.map(x => Math.max(x.likes, x.comments, x.shares)), 1);
  }

  getX(i: number): number {
    const d = this.chartData();
    if (d.length <= 1) return this.svgW / 2;
    return (i / (d.length - 1)) * this.svgW;
  }

  getY(val: number): number {
    const h = this.svgH - this.padding.top - this.padding.bottom;
    return this.padding.top + (1 - val / this.chartMax) * h;
  }

  getLinePath(field: 'likes' | 'comments' | 'shares'): string {
    const d = this.chartData();
    if (d.length === 0) return '';
    return d.map((pt, i) => `${i === 0 ? 'M' : 'L'} ${this.getX(i)} ${this.getY(pt[field])}`).join(' ');
  }
}
