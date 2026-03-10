import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { User } from '../../shared/models/models';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(private http: HttpClient) { }

  // GET /api/users/me?userId=...
  getMe(userId: number): Observable<User> {
    const params = new HttpParams().set('userId', userId);
    return this.http.get<User>('/api/users/me', { params });
  }

  // GET /api/users/{userId}
  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`/api/users/${id}`);
  }

  // GET /api/users/username/{username}
  getUserByUsername(username: string): Observable<User> {
    return this.http.get<User>(`/api/users/username/${username}`);
  }

  // PUT /api/users/{userId}
  updateProfile(id: number, data: {
    bio?: string; location?: string; website?: string;
    displayName?: string; isPrivate?: boolean;
    industry?: string; category?: string;
    businessAddress?: string; businessHours?: string; contactEmail?: string;
  }): Observable<User> {
    return this.http.put<User>(`/api/users/${id}`, data);
  }

  // PUT /api/users/{userId}/privacy  — { isPrivateAccount: boolean }
  updatePrivacy(id: number, isPrivateAccount: boolean): Observable<void> {
    return this.http.put<void>(`/api/users/${id}/privacy`, { isPrivateAccount });
  }

  // DELETE /api/users/{userId}
  deleteProfile(id: number): Observable<void> {
    return this.http.delete<void>(`/api/users/${id}`);
  }

  // GET /api/users/search?keyword=
  searchUsers(query: string): Observable<User[]> {
    const params = new HttpParams().set('keyword', query);
    return this.http.get<User[]>('/api/users/search', { params }).pipe(
      catchError(() => of([]))
    );
  }

  // GET /api/users/suggestions?userId=
  getSuggestions(userId: number): Observable<User[]> {
    const params = new HttpParams().set('userId', userId);
    return this.http.get<User[]>('/api/users/suggestions', { params }).pipe(
      catchError(() => of([]))
    );
  }

  // POST /api/users/{userId}/profile-picture (S3)
  uploadProfilePicture(id: number, file: File): Observable<{ url: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ url: string }>(`/api/users/${id}/profile-picture`, formData);
  }
}
