import { Injectable, inject } from '@angular/core';
import { KeycloakEventType, KeycloakOptions, KeycloakService } from 'keycloak-angular';
import { KeycloakLoginOptions } from 'keycloak-js';
import { LogService } from './log-api.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService extends KeycloakService {

    private logService = inject(LogService);

    constructor() {
        super();
        this.keycloakEvents$.subscribe((event) => {
            if (event.type == KeycloakEventType.OnAuthSuccess) {
                this.logService.logLogin().subscribe();
            } else if (event.type == KeycloakEventType.OnTokenExpired) {
                super.updateToken(20);
            }
            return;
        })
    }

    override init(options?: KeycloakOptions | undefined): Promise<boolean> {
        const result = super.init(options);
        return result;
    }

    override login(options?: KeycloakLoginOptions | undefined): Promise<void> {
        const result = super.login(options);
        return result;
    }

    override logout(redirectUri?: string | undefined): Promise<void> {
        this.logService.logLogout().subscribe();
        return super.logout(redirectUri);
    }

}
