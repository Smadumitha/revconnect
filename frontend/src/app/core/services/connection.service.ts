import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Connection, Follower } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class ConnectionService {
  constructor(private http: HttpClient) { }

  // POST /connections/request  { senderId, receiverId }
  sendRequest(senderId: number, receiverId: number): Observable<Connection> {
    return this.http.post<Connection>('/connections/request', { senderId, receiverId });
  }

  // PUT /connections/accept/{requestId}
  acceptRequest(requestId: number): Observable<Connection> {
    return this.http.put<Connection>(`/connections/accept/${requestId}`, {});
  }

  // PUT /connections/reject/{requestId}
  rejectRequest(requestId: number): Observable<Connection> {
    return this.http.put<Connection>(`/connections/reject/${requestId}`, {});
  }

  // DELETE /connections/unfollow?followerId=&followingId=
  unfollow(followerId: number, followingId: number): Observable<void> {
    const params = new HttpParams().set('followerId', followerId).set('followingId', followingId);
    return this.http.delete<void>('/connections/unfollow', { params });
  }

  // GET /connections/followers/{userId}
  getFollowers(userId: number): Observable<Follower[]> {
    return this.http.get<Follower[]>(`/connections/followers/${userId}`).pipe(
      catchError(() => of([]))
    );
  }

  // GET /connections/following/{userId}
  getFollowing(userId: number): Observable<Follower[]> {
    return this.http.get<Follower[]>(`/connections/following/${userId}`).pipe(
      catchError(() => of([]))
    );
  }

  // GET /connections/mutual?user1=&user2=
  getMutualConnections(user1: number, user2: number): Observable<Follower[]> {
    const params = new HttpParams().set('user1', user1).set('user2', user2);
    return this.http.get<Follower[]>('/connections/mutual', { params }).pipe(
      catchError(() => of([]))
    );
  }

  // GET /connections?userId=  => returns List<Long>
  getConnections(userId: number): Observable<number[]> {
    const params = new HttpParams().set('userId', userId);
    return this.http.get<number[]>('/connections', { params }).pipe(
      catchError(() => of([]))
    );
  }

  // GET /connections/pending/received?userId=
  getPendingReceived(userId: number): Observable<Connection[]> {
    const params = new HttpParams().set('userId', userId);
    return this.http.get<Connection[]>('/connections/pending/received', { params }).pipe(
      catchError(() => of([]))
    );
  }

  // GET /connections/pending/sent?userId=
  getPendingSent(userId: number): Observable<Connection[]> {
    const params = new HttpParams().set('userId', userId);
    return this.http.get<Connection[]>('/connections/pending/sent', { params }).pipe(
      catchError(() => of([]))
    );
  }

  // DELETE /connections/{id}
  removeConnection(id: number): Observable<void> {
    return this.http.delete<void>(`/connections/${id}`);
  }

  // Check if two users are connected
  checkConnection(userId: number, targetId: number): Observable<boolean> {
    return this.http.get<boolean>(`/connections/check?userId=${userId}&targetId=${targetId}`).pipe(
      catchError(() => of(false))
    );
  }

  blockUser(userId: number): Observable<void> {
    return of(undefined);
  }

  unblockUser(userId: number): Observable<void> {
    return of(undefined);
  }

  getConnectionStatus(userId: number, targetId: number): Observable<any> {
    const params = new HttpParams().set('userId', userId).set('targetId', targetId);
    return this.http.get<any>('/connections/status', { params }).pipe(
      catchError(() => of({ isFollowing: false, isConnected: false, isPendingSent: false, isPendingReceived: false }))
    );
  }
}
