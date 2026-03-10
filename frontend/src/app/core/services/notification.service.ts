import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, interval, Subscription, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { Notification, NotificationPreferences, PageResponse } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  unreadCount = signal<number>(0);
  private pollSub: Subscription | null = null;

  constructor(private http: HttpClient) { }

  // GET /api/notifications/{userId}?page=&size=
  getNotifications(userId: number, page = 0, size = 20): Observable<PageResponse<Notification>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Notification>>(`/api/notifications/${userId}`, { params });
  }

  // GET /api/notifications/unread-count?userId=
  getUnreadCount(userId: number): Observable<number> {
    const params = new HttpParams().set('userId', userId);
    return this.http.get<number>('/api/notifications/unread-count', { params }).pipe(
      catchError(() => of(0))
    );
  }

  // PUT /api/notifications/read/{notificationId}
  markAsRead(id: number): Observable<string> {
    return this.http.put(`/api/notifications/read/${id}`, {}, { responseType: 'text' });
  }

  // PUT /api/notifications/read-all?userId=
  markAllAsRead(userId: number): Observable<string> {
    const params = new HttpParams().set('userId', userId);
    return this.http.put('/api/notifications/read-all', {}, { params, responseType: 'text' });
  }

  // GET /api/notifications/preferences/{userId}
  getPreferences(userId: number): Observable<NotificationPreferences> {
    return this.http.get<NotificationPreferences>(`/api/notifications/preferences/${userId}`).pipe(
      catchError(() => of({ connectionRequests: true, postLikes: true, postComments: true, postShares: true, newFollowers: true }))
    );
  }

  // PUT /api/notifications/preferences/{userId}
  updatePreferences(userId: number, prefs: NotificationPreferences): Observable<NotificationPreferences> {
    return this.http.put<NotificationPreferences>(`/api/notifications/preferences/${userId}`, prefs).pipe(
      catchError(() => of(prefs))
    );
  }

  refreshCount(userId: number): void {
    this.getUnreadCount(userId).subscribe(count => this.unreadCount.set(count));
  }

  // ── POLLING-BASED NOTIFICATION CHECK (replaces broken SSE) ──
  // Polls every 60 seconds instead of SSE which causes 400 errors
  startPolling(userId: number): void {
    this.stopPolling();
    // Initial fetch
    this.refreshCount(userId);
    // Poll every 60 seconds
    this.pollSub = interval(60000).pipe(
      switchMap(() => this.getUnreadCount(userId).pipe(catchError(() => of(0))))
    ).subscribe(count => this.unreadCount.set(count));
  }

  stopPolling(): void {
    if (this.pollSub) {
      this.pollSub.unsubscribe();
      this.pollSub = null;
    }
  }

  // Keep old method name for backward compat but now uses polling
  listenForNotifications(userId: number): void {
    this.startPolling(userId);
  }
}
