import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {environment} from '../../environments/environment';
import {MessageService} from './message.service';
import {Event} from "../model/event";
import {PRECONDITION_FAILED} from "http-status-codes";


@Injectable()
export class EventService {

    constructor(private translateService: TranslateService, private httpClient: HttpClient, private messageService: MessageService) {
    }

    findAll(): Observable<Event[]> {
        return this.httpClient.get<Event[]>(`${environment.zeusServerScenarioApiBaseUri}/events`);
    }

    findById(id: String): Observable<Event> {
        return this.httpClient.get<Event>(`${environment.zeusServerScenarioApiBaseUri}/events/${id}`);
    }

    save(event: Event): Observable<Event> {
        let message;
        let updatedEvent: Event = null;
        return this.httpClient.put<Event>(`${environment.zeusServerScenarioApiBaseUri}/events/${event.id}`, event,
            {headers: new HttpHeaders().set('If-Match', `${event.version}`)})
            .pipe(catchError((error: HttpErrorResponse) => {
                if (error.status === PRECONDITION_FAILED) {
                    this.translateService.get('Data has not been updated due to concurrent modifications.')
                        .subscribe(result => message = result);
                    this.messageService.displayWarning(message);
                    updatedEvent = error.error;
                    updatedEvent.$error = true;
                    return of(updatedEvent);
                } else {
                    this.translateService.get('Sorry, an unexpected error happened !')
                        .subscribe(result => message = result);
                    this.messageService.displayError(message);
                    event.$error = true;
                    return of(event);
                }
            }))
            .pipe(tap(reloadedEvent => {
                event.$editing = false;
                if (!reloadedEvent.$error) {
                    this.translateService.get('The event \'{name}\' has been updated.', {name: event.name})
                        .subscribe(result => message = result);
                    this.messageService.displayInfo(message);
                }
            }));
    }

    fire(event: Event): Observable<Event> {
        let message;
        this.translateService.get('Event has successfully been fired.')
            .subscribe(result => message = result);
        return this.httpClient.post<Event>(`${environment.zeusServerScenarioApiBaseUri}/events/${event.id}!fire`, {})
            .pipe(tap(() => this.messageService.displayInfo(message)));
    }

}
