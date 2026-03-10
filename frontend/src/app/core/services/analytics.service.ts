import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

export interface UserAnalytics {
    totalLikes: number;
    totalComments: number;
    totalShares: number;
    totalProfileViews: number;
    totalImpressions: number;
    engagementRate: number;
}

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
    constructor(private http: HttpClient) { }

    getUserAnalytics(userId: number, followers: number = 0): Observable<UserAnalytics | null> {
        const params = new HttpParams().set('followers', followers.toString());
        return this.http.get<any>(`/api/analytics/user/${userId}`, { params }).pipe(
            map(res => res.data),
            catchError(() => of(null))
        );
    }

    trackProfileView(userId: number): Observable<any> {
        return this.http.post(`/api/analytics/view/profile/${userId}`, {}).pipe(
            catchError(() => of(null))
        );
    }
}
