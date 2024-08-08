import { Directive, ElementRef, Input } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[permission]',
  standalone: true,
})
export class PermissionDirective {
  constructor(
    private keycloak: AuthService,
    private elementRef: ElementRef,
  ) {}

  @Input() set permission(permission: string[]) {
    if (!permission.some((p) => this.keycloak.isUserInRole(p))) {
      this.elementRef.nativeElement.remove();
      this.elementRef.nativeElement.style.display = 'none'; // When can't remove, hide it
    }
  }
}
