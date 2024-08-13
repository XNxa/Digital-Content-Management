import { Injectable } from '@angular/core';
import { KeycloakBearerInterceptor, KeycloakService } from 'keycloak-angular';
import { AuthService } from '../services/auth.service';
import { HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthBearerInterceptor extends KeycloakBearerInterceptor {
  
  private ks : KeycloakService;

  constructor (keycloakService: AuthService) {
    super(keycloakService);
    this.ks = keycloakService;
  }

  override intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    this.ks.updateToken(60);
    return super.intercept(req, next);
  }
}
