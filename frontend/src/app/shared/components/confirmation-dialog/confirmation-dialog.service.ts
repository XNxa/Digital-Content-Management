import {
  ApplicationRef,
  ComponentRef,
  EmbeddedViewRef,
  Injectable,
  createComponent,
} from '@angular/core';
import { ConfirmationDialogComponent } from './confirmation-dialog.component';

@Injectable({
  providedIn: 'root',
})
export class ConfirmationDialogService {
  constructor(private appRef: ApplicationRef) {}

  openConfirmationDialog(title: string, text: string): Promise<boolean> {
    return new Promise((resolve) => {
      // Create a component reference from the component
      const componentRef: ComponentRef<ConfirmationDialogComponent> =
        createComponent(ConfirmationDialogComponent, {
          environmentInjector: this.appRef.injector,

        });

      // Get the DOM element from the component
      const domElem = (componentRef.hostView as EmbeddedViewRef<any>)
        .rootNodes[0] as HTMLElement;

      // Append the DOM element to the body
      document.body.appendChild(domElem);

      componentRef.setInput('title', title);
      componentRef.setInput('text', text);

      componentRef.instance.init();

      // Listen to the confirmation event
      componentRef.instance.confirmed.subscribe((result: boolean) => {
        resolve(result);
        this.appRef.detachView(componentRef.hostView);
        componentRef.destroy();
      });
    });
  }
}
