// ─────────────────────────────────────────────
// USER MODELS
// ─────────────────────────────────────────────

export interface User {
  id: number;
  userId: number;              // backend returns userId
  username: string;
  email: string;
  displayName: string;
  bio?: string;
  profilePicture?: string;     // S3 URL after enhancement
  location?: string;
  website?: string;
  phone?: string;
  role: 'PERSONAL' | 'CREATOR' | 'BUSINESS';
  privacy?: 'PUBLIC' | 'PRIVATE';
  isPrivate?: boolean;
  // Enhanced fields (added after backend enhancement)
  category?: string;
  industry?: string;
  businessAddress?: string;
  businessHours?: string;
  contactEmail?: string;
  followersCount: number;
  followingCount: number;
  isFollowing?: boolean;       // populated when viewing another user's profile
  isPendingSent?: boolean;
  isPendingReceived?: boolean;
  isBlocked?: boolean;         // block feature
  emailVerified?: boolean;     // email verification enhancement
  securityQuestion?: string;
  createdAt?: string;
}

// ─────────────────────────────────────────────
// POST MODELS
// ─────────────────────────────────────────────

export interface Post {
  id: number;
  userId: number;
  content: string;
  mediaUrl?: string;           // S3 URL after enhancement
  author?: User;               // populated via Feign from user-service
  type: 'TEXT' | 'IMAGE' | 'SHARE' | 'PROMOTIONAL';
  status: 'PUBLISHED' | 'DRAFT' | 'SCHEDULED';
  hashtags: string[];
  productTags?: string[];
  likesCount: number;
  commentsCount: number;
  sharesCount?: number;
  isLiked: boolean;
  isShared?: boolean;
  pinned: boolean;
  promotional?: boolean;
  ctaText?: string;
  ctaUrl?: string;
  scheduledAt?: string;
  originalPost?: Post;         // for reposts
  createdAt: string;
  updatedAt?: string;
}

// ─────────────────────────────────────────────
// COMMENT MODEL
// ─────────────────────────────────────────────

export interface Comment {
  id: number;
  content: string;
  userId: number;
  author?: User;               // populated via Feign
  postId: number;
  parentCommentId?: number;    // for nested/threaded comments
  createdAt: string;
}

// ─────────────────────────────────────────────
// NOTIFICATION MODEL
// ─────────────────────────────────────────────

export interface Notification {
  id: number;
  userId: number;
  sender?: User;
  type: 'CONNECTION_REQUEST' | 'CONNECTION_ACCEPTED' | 'NEW_FOLLOWER'
  | 'POST_LIKED' | 'POST_COMMENTED' | 'POST_SHARED' | 'POST_REPOSTED';
  message: string;
  referenceId?: number;
  read: boolean;
  createdAt: string;
}

export interface NotificationPreferences {
  connectionRequests: boolean;
  postLikes: boolean;
  postComments: boolean;
  postShares: boolean;
  newFollowers: boolean;
}

// ─────────────────────────────────────────────
// CONNECTION MODEL
// ─────────────────────────────────────────────

export interface Connection {
  id: number;
  senderId: number;
  receiverId: number;
  requester: User;
  recipient: User;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  createdAt: string;
}

export interface Follower {
  id: number;
  followerId: number;
  followingId: number;
  createdAt: string;
}

// ─────────────────────────────────────────────
// AUTH MODELS
// ─────────────────────────────────────────────

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  userId: number;
  username: string;
  email: string;
  displayName: string;
  profilePicture?: string;
  role: 'PERSONAL' | 'CREATOR' | 'BUSINESS';
  emailVerified?: boolean;
  securityQuestion?: string;
}

// ─────────────────────────────────────────────
// API RESPONSE WRAPPER
// Used by: interaction-service, analytics, comments, notifications
// NOTE: Auth-service returns AuthResponse directly (no wrapper)
// ─────────────────────────────────────────────

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
}

// ─────────────────────────────────────────────
// PAGINATION
// ─────────────────────────────────────────────

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  last: boolean;
  first: boolean;
}

// ─────────────────────────────────────────────
// ANALYTICS MODEL
// ─────────────────────────────────────────────

export interface Analytics {
  id: number;
  postId: number;
  eventType: string;
  userId: number;
  createdAt: string;
}

export interface EngagementStats {
  postId: number;
  likesCount: number;
  commentsCount: number;
  sharesCount: number;
  engagementRate: number;
}

// ─────────────────────────────────────────────
// VALIDATION ERROR MODEL
// ─────────────────────────────────────────────

export interface ValidationError {
  field: string;
  message: string;
}

export interface ErrorResponse {
  success: false;
  message: string;
  errors?: ValidationError[];
  timestamp: string;
}
