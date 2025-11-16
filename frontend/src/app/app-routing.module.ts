import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { LoginComponent } from './auth/components/login/login.component';
import { AdminHomeComponent } from './admin/pages/admin-home/admin-home.component';
import { UserHomeComponent } from './user/pages/user-home/user-home.component';

import { AuthGuard } from './core/guards/auth.guard';
import { AdminGuard } from './core/guards/admin.guard';

const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },

  {
    path: 'admin',
    component: AdminHomeComponent,
    canActivate: [AuthGuard, AdminGuard]
  },

  {
    path: 'user',
    component: UserHomeComponent,
    canActivate: [AuthGuard]
  },

  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },

  {
    path: '**',
    redirectTo: '/login'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
