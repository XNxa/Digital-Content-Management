import { Directive, ElementRef, Input } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[permission]',
  standalone: true,
})
export class PermissionDirective {
  constructor(
    private keycloak: KeycloakService,
    private elementRef: ElementRef,
  ) {}

  @Input() set permission(permission: string[]) {
    if (!permission.some((p) => this.keycloak.isUserInRole(p))) {
      this.elementRef.nativeElement.remove();
      this.elementRef.nativeElement.style.display = 'none'; // When can't remove, hide it
    }
  }
}
