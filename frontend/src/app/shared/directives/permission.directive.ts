import { Directive, ElementRef, Input, ViewContainerRef } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Directive({
  selector: '[permission]',
  standalone: true
})
export class PermissionDirective {

  constructor(private keycloak: KeycloakService, private elementRef: ElementRef) { }
  
  @Input() set permission(permission: string) {
    if (!this.keycloak.isUserInRole(permission)) {
      this.elementRef.nativeElement.remove();
      this.elementRef.nativeElement.style.display = 'none'; // When can't remove, hide it
    }
  };

}
