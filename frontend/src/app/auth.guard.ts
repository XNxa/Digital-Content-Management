import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { KeycloakAuthGuard, KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard extends KeycloakAuthGuard {
  constructor(
    protected override router: Router,
    protected override keycloakAngular: KeycloakService,
  ) {
    super(router, keycloakAngular);
  }

  override isAccessAllowed(
    route: ActivatedRouteSnapshot,
  ): Promise<boolean | UrlTree> {
    return new Promise((resolve) => {
      if (!this.authenticated) {
        this.keycloakAngular.login();
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
