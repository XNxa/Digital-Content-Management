import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { SnackbarService } from '../shared/components/snackbar/snackbar.service';

@Injectable()
export class ErrorHandlerInterceptor implements HttpInterceptor {
  constructor(private snackbar: SnackbarService) {}

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler,
  ): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      tap({
        error: (error: HttpErrorResponse) => {
          if (error.status == 403) {
            this.snackbar.show(
              'You are not authorized to access this resource',
            );
            return;
          }

          this.snackbar.show(
            error.error.code + ' : ' + this.getErrorMessage(error.error.code),
          );
        },
      }),
    );
  }

  private getErrorMessage(code: string): string {
    switch (code) {
      case 'INTERNAL.SERVER.ERROR':
        return 'An internal server error occurred. Please try again later.';
      case 'FILE.NOT.FOUND':
        return 'The requested file could not be found.';
      case 'NO.THUMBNAIL':
        return 'No thumbnail available.';
      case 'BAD.REQUEST':
        return 'The request was invalid. Please check your input.';
      case 'CONSTRAINT.VIOLATION':
        return 'A constraint violation occurred.';
      case 'REQUIERED.FIELD.MISSING':
        return 'A required field is missing.';
      case 'EXCEED.MAX.SIZE':
        return 'The provided text exceed the maximum allowed size.';
      case 'INVALID.MIME.TYPE':
        return 'The uploaded file has an invalid MIME type.';
      case 'INVALID.EMAIL':
        return 'The provided email address is invalid.';
      case 'INVALID.FOLDER':
        return 'The provided folder name or format is invalid.';
      default:
        return 'An unknown error occurred.';
    }
  }
}
