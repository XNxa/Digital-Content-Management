import { Injectable } from "@angular/core";
import { SnackbarComponent } from "./snackbar.component";

@Injectable({
    providedIn: 'root'
})
export class SnackbarService {
    
    private snackbarComponent !: SnackbarComponent;

    constructor() { }

    setSnackbarComponent(snackbarComponent: SnackbarComponent) {
        this.snackbarComponent = snackbarComponent;
    }

    show(message: string, duration: number = 3000) {
        this.snackbarComponent.show(message, duration);
    }
    
}