import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { ConnectionService } from '../../core/services/connection.service';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { Connection, User } from '../../shared/models/models';

@Component({
  selector: 'app-connections',
  standalone: true,
  imports: [CommonModule, NavbarComponent, RouterLink],
  templateUrl: './connections.component.html',
  styleUrls: ['./connections.component.css']
})
export class ConnectionsComponent implements OnInit {
  connections = signal<Connection[]>([]);
  pendingReceived = signal<Connection[]>([]);
  pendingSent = signal<Connection[]>([]);
  tab = signal<'connections' | 'pending' | 'sent'>('connections');
  loading = signal(false);

  constructor(
    private connectionService: ConnectionService,
    private userService: UserService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId();
    if (!userId) return;

    this.connectionService.getPendingReceived(userId).subscribe(r => this.pendingReceived.set(r));
    this.connectionService.getPendingSent(userId).subscribe(r => this.pendingSent.set(r));

    // Backend returns List<Long> (user IDs) for accepted connections
    this.connectionService.getConnections(userId).pipe(
      switchMap(ids => {
        if (!ids || ids.length === 0) return of([]);
        const userCalls = (ids as number[]).map(id => this.userService.getUserById(id).pipe(
          catchError(() => of({ id, userId: id, displayName: `RevConnect User`, username: null } as any))
        ));
        return forkJoin(userCalls);
      })
    ).subscribe(users => {
      const mapped = users.map(u => ({ id: u.id || u.userId, requester: u, status: 'ACCEPTED' } as any));
      this.connections.set(mapped);
    });

    // Followers and Following unified into Connections
  }

  accept(id: number): void {
    this.connectionService.acceptRequest(id).subscribe(() => {
      const conn = this.pendingReceived().find(c => c.id === id);
      if (conn) {
        this.pendingReceived.update(l => l.filter(c => c.id !== id));
        const targetUser = conn.requester;
        this.connections.update(l => [...l, {
          id: targetUser?.userId || targetUser?.id,
          requester: targetUser,
          status: 'ACCEPTED'
        } as any]);
      }
    });
  }

  reject(id: number): void {
    this.connectionService.rejectRequest(id).subscribe(() => {
      this.pendingReceived.update(l => l.filter(c => c.id !== id));
    });
  }

  removeConnection(targetId: number): void {
    if (!confirm('Are you sure you want to remove this connection?')) return;
    const myId = this.authService.getCurrentUserId();
    if (!myId) return;

    this.connectionService.unfollow(myId, targetId).subscribe({
      next: () => {
        this.connections.update(l => l.filter(c => (c.id !== targetId && (c as any).userId !== targetId)));
      },
      error: () => {
        // Fallback: If it's a mutual connection, we might need to unfollow both ways 
        // but typically one way is enough to break the connection in this logic.
        this.connections.update(l => l.filter(c => (c.id !== targetId && (c as any).userId !== targetId)));
      }
    });
  }


  getConnectionUser(c: any): any {
    const myId = this.authService.getCurrentUserId();
    if (!c) return null;

    // 1. Check for nested objects (from Connections tab)
    if (c.requester && c.recipient) {
      const other = (c.requester.userId === myId || c.requester.id === myId) ? c.recipient : c.requester;
      return other;
    }

    // 2. Fallback for pending requests
    if (c.requester) return c.requester;
    if (c.recipient) return c.recipient;

    // 3. Fallback for raw IDs (from getConnections)
    if (typeof c === 'number' || (c.id && !c.requester)) {
      const id = c.id || c;
      return {
        displayName: 'RevConnect User',
        username: null,
        id: id,
        userId: id
      };
    }
    return null;
  }

  safeUsername(user: any): string {
    return user?.username || '';
  }

  safeDisplayName(user: any): string {
    return user?.displayName || 'Unknown User';
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
}
