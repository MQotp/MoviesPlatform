import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | Observable<boolean> | Promise<boolean> {

    const role = this.authService.getUserRole();

    if (role === 'ADMIN') {
      return true;
    }

    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/user']);
    } else {
      this.router.navigate(['/login']);
    }

    return false;
  }
}
