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
                    if (error.status == 403) {
                        this.snackbar.show('You are not authorized to access this resource');
                        return;
                    }

                    this.snackbar.show(error.error.code + ' : ' + error.error.message);
                }
            })
        );
    }
    
}