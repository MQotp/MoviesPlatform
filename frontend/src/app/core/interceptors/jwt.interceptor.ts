import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';
import { API_BASE_URL } from '../../config/api-urls';


@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    const token = this.authService.getToken();

    const isApiRelative = req.url.indexOf('/api/') === 0;
    const isApiAbsolute = req.url.indexOf(API_BASE_URL) === 0;

    if (token && (isApiRelative || isApiAbsolute)) {
      const authReq = req.clone({
        setHeaders: {
          Authorization: 'Bearer ' + token
        }
      });

      return next.handle(authReq);
    }

    return next.handle(req);
  }
}
