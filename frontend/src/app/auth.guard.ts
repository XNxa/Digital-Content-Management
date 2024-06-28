import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { KeycloakAuthGuard, KeycloakService } from 'keycloak-angular';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard extends KeycloakAuthGuard {

  constructor(protected override router: Router, protected override keycloakAngular: KeycloakService) {
    super(router, keycloakAngular);
  }

  override isAccessAllowed(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean | UrlTree> {
    return new Promise(async (resolve, reject) => {
      if (!this.authenticated) {
        this.keycloakAngular.login();
        return;
      }

      const requiredRoles = route.data['roles'];
      if (!requiredRoles || requiredRoles.length === 0) {
        resolve(true);
        return;
      }

      const hasRole = requiredRoles.every((role: string) => this.roles.includes(role));
      resolve(hasRole);
    });
  }

}

