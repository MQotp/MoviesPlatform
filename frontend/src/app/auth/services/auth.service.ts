import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { API_URLS } from '../../config/api-urls';

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  role?: string; // optional field if backend returns role
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly TOKEN_KEY = 'moviesPlatformToken';
  private readonly ROLE_KEY = 'moviesPlatformRole';

  constructor(private http: HttpClient) { }

  login(credentials: LoginCredentials): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(API_URLS.AUTH.LOGIN, credentials)
      .pipe(
        tap((response: LoginResponse) => {
          // Store token
          localStorage.setItem(this.TOKEN_KEY, response.token);

          // Store role if backend sends it
          if (response.role) {
            localStorage.setItem(this.ROLE_KEY, response.role);
          }
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.ROLE_KEY);
  }

  getToken(): string {
    const token = localStorage.getItem(this.TOKEN_KEY);
    if (token === null) {
      return '';
    }
    return token;
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    return token !== null && token !== '';
  }

  getUserRole(): string {
    const role = localStorage.getItem(this.ROLE_KEY);
    if (role === null) {
      return '';
    }
    return role;
  }
}
