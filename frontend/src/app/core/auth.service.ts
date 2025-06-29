import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, tap } from 'rxjs';
import { Router } from '@angular/router';

interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}
interface LoginRequest {
  email: string;
  password: string;
}
interface JwtAuthResponse {
  accessToken: string;
  tokenType: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth';

  private token$ = new BehaviorSubject<string | null>(null);
  private email$ = new BehaviorSubject<string | null>(null);
  public userEmail$ = this.email$.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    if (isPlatformBrowser(this.platformId)) {
      const savedToken = sessionStorage.getItem('authToken');
      const savedEmail = sessionStorage.getItem('authEmail');
      this.token$.next(savedToken);
      this.email$.next(savedEmail);
    }
  }

  register(data: RegisterRequest) {
    return this.http
      .post<JwtAuthResponse>(`${this.baseUrl}/register`, data)
      .pipe(
        tap((res) => {
          if (isPlatformBrowser(this.platformId)) {
            sessionStorage.setItem('authToken', res.accessToken);
            sessionStorage.setItem('authEmail', data.email);
          }
          this.token$.next(res.accessToken);
          this.email$.next(data.email);
        })
      );
  }

  login(data: LoginRequest) {
    return this.http.post<JwtAuthResponse>(`${this.baseUrl}/login`, data).pipe(
      tap((res) => {
        if (isPlatformBrowser(this.platformId)) {
          sessionStorage.setItem('authToken', res.accessToken);
          sessionStorage.setItem('authEmail', data.email);
        }
        this.token$.next(res.accessToken);
        this.email$.next(data.email);
        this.router.navigate(['/projects']);
      })
    );
  }

  logout() {
    if (isPlatformBrowser(this.platformId)) {
      sessionStorage.removeItem('authToken');
      sessionStorage.removeItem('authEmail');
    }
    this.token$.next(null);
    this.email$.next(null);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return this.token$.value;
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
