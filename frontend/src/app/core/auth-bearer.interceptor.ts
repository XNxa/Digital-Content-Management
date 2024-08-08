import { Injectable } from '@angular/core';
import { KeycloakBearerInterceptor } from 'keycloak-angular';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthBearerInterceptor extends KeycloakBearerInterceptor {
  constructor (keycloakService: AuthService) {
    super(keycloakService);
  }
}
