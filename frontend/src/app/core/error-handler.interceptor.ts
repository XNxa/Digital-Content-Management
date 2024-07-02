import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, tap } from "rxjs";
import { SnackbarService } from "../shared/components/snackbar/snackbar.service";

@Injectable()
export class ErrorHandlerInterceptor implements HttpInterceptor {
    

    constructor(private snackbar : SnackbarService) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(req).pipe(
            tap({
                error: (error : HttpErrorResponse) => {
                    this.snackbar.show(error.error.code + ' : ' + error.error.message);
                }
            })
        );
    }
    
}