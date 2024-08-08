import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  UrlTree,
} from '@angular/router';
import { KeycloakAuthGuard } from 'keycloak-angular';
import { AuthService } from './services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard extends KeycloakAuthGuard {
  constructor(
    protected override router: Router,
    protected override keycloakAngular: AuthService,
  ) {
    super(router, keycloakAngular);
  }

  override isAccessAllowed(
    route: ActivatedRouteSnapshot,
  ): Promise<boolean | UrlTree> {
    return new Promise((resolve) => {
      if (!this.authenticated) {
        this.keycloakAngular.login({
          redirectUri: window.location.origin + this.router.url
        });
        return;
      }

      const requiredRoles = route.data['roles'];
      if (!requiredRoles || requiredRoles.length === 0) {
        resolve(true);
        return;
      }

      const hasRole = requiredRoles.some((role: string) =>
        this.roles.includes(role),
      );
      resolve(hasRole);
    });
  }
}
