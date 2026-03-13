import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Analytics, ApiResponse, Comment, EngagementStats, PageResponse, Post } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class PostService {
  constructor(private http: HttpClient) { }

  // POST /posts
  createPost(data: {
    userId: number; content: string; mediaUrl?: string;
    type?: 'TEXT' | 'IMAGE' | 'SHARE' | 'PROMOTIONAL';
    hashtags?: string[]; productTags?: string[]; promotional?: boolean;
    pinned?: boolean; ctaText?: string; ctaUrl?: string; scheduledAt?: string;
  }): Observable<Post> {
    return this.http.post<Post>('/posts', data);
  }

  // PUT /posts/{id}
  updatePost(id: number, data: any): Observable<Post> {
    return this.http.put<Post>(`/posts/${id}`, data);
  }

  // DELETE /posts/{id}
  deletePost(id: number): Observable<string> {
    return this.http.delete(`/posts/${id}`, { responseType: 'text' });
  }

  // GET /posts/{id}
  getPost(id: number): Observable<Post> {
    return this.http.get<Post>(`/posts/${id}`);
  }

  // GET /posts/{postId}/owner
  getPostOwner(postId: number): Observable<number> {
    return this.http.get<number>(`/posts/${postId}/owner`);
  }

  // GET /posts/user/{userId}
  getUserPosts(userId: number, page: number = 0, size: number = 10): Observable<PageResponse<Post>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Post>>(`/posts/user/${userId}`, { params }).pipe(
      catchError(() => of({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 0, last: true, first: true }))
    );
  }

  // GET /feed/home?userId=
  getHomeFeed(userId: number, page: number = 0, size: number = 10): Observable<PageResponse<Post>> {
    const params = new HttpParams().set('userId', userId).set('page', page).set('size', size);
    return this.http.get<PageResponse<Post>>('/feed/home', { params }).pipe(
      catchError(() => of({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 0, last: true, first: true }))
    );
  }

  // GET /feed/trending
  getTrending(page: number = 0, size: number = 10): Observable<PageResponse<Post>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<Post>>('/feed/trending', { params }).pipe(
      catchError(() => of({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 0, last: true, first: true }))
    );
  }

  // GET /feed/promotional
  getPromotionalPosts(): Observable<Post[]> {
    return this.http.get<Post[]>('/feed/promotional').pipe(
      catchError(() => of([]))
    );
  }

  // GET /feed/hashtag?tag=
  getPostsByHashtag(tag: string, page: number = 0, size: number = 10): Observable<PageResponse<Post>> {
    const params = new HttpParams().set('tag', tag).set('page', page).set('size', size);
    return this.http.get<PageResponse<Post>>('/feed/hashtag', { params }).pipe(
      catchError(() => of({ content: [], totalElements: 0, totalPages: 0, number: 0, size: 0, last: true, first: true }))
    );
  }

  // GET /feed/trending-tags
  getTrendingTags(): Observable<string[]> {
    return this.http.get<string[]>('/feed/trending-tags').pipe(
      catchError(() => of([]))
    );
  }

  // POST /api/interactions/like  { userId, postId }
  likePost(userId: number, postId: number): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>('/api/interactions/like', { userId, postId });
  }

  // DELETE /api/interactions/unlike?userId=&postId=
  unlikePost(userId: number, postId: number): Observable<ApiResponse<string>> {
    const params = new HttpParams().set('userId', userId).set('postId', postId);
    return this.http.delete<ApiResponse<string>>('/api/interactions/unlike', { params });
  }

  // GET /api/interactions/post/{postId}/likers
  getLikerNames(postId: number): Observable<string[]> {
    return this.http.get<string[]>(`/api/interactions/post/${postId}/likers`).pipe(
      catchError(() => of([]))
    );
  }

  // POST /api/shares  { userId, postId }
  sharePost(userId: number, postId: number): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>('/api/shares', { userId, postId });
  }

  // DELETE /api/shares?userId=&postId=
  undoShare(userId: number, postId: number): Observable<ApiResponse<string>> {
    const params = new HttpParams().set('userId', userId).set('postId', postId);
    return this.http.delete<ApiResponse<string>>('/api/shares', { params });
  }

  // POST /api/shares/repost  { userId, postId, content? }
  repost(userId: number, postId: number, content?: string): Observable<Post> {
    return this.http.post<Post>('/api/shares/repost', { userId, postId, content });
  }

  // POST /api/comments  { userId, postId, content, parentCommentId }
  addComment(userId: number, postId: number, content: string, parentCommentId?: number): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>('/api/comments', {
      userId, postId, content, parentCommentId: parentCommentId ?? null
    });
  }

  // GET /api/comments/post/{postId}
  getComments(postId: number): Observable<ApiResponse<Comment[]>> {
    return this.http.get<ApiResponse<Comment[]>>(`/api/comments/post/${postId}`).pipe(
      catchError(() => of({ success: true, data: [] as Comment[], timestamp: '' }))
    );
  }

  // DELETE /api/comments/{commentId}
  deleteComment(commentId: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`/api/comments/${commentId}`);
  }

  // GET /api/analytics/post/{postId}
  getPostAnalytics(postId: number): Observable<ApiResponse<Analytics[]>> {
    return this.http.get<ApiResponse<Analytics[]>>(`/api/analytics/post/${postId}`).pipe(
      catchError(() => of({ success: true, data: [] as Analytics[], timestamp: '' }))
    );
  }

  // GET /api/analytics/engagement/{postId}?followers=
  getEngagementRate(postId: number, followers: number): Observable<ApiResponse<number>> {
    const params = new HttpParams().set('followers', followers);
    return this.http.get<ApiResponse<number>>(`/api/analytics/engagement/${postId}`, { params }).pipe(
      catchError(() => of({ success: true, data: 0, timestamp: '' }))
    );
  }

  // POST /posts/{postId}/media
  uploadMedia(postId: number, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`/posts/${postId}/media`, formData, { responseType: 'text' });
  }

  // GET /posts/drafts?userId=
  getDrafts(userId: number): Observable<Post[]> {
    return of([]);
  }

  // GET /posts/scheduled?userId=
  getScheduledPosts(userId: number): Observable<Post[]> {
    return of([]);
  }
}
