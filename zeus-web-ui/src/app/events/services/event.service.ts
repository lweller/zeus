import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {EMPTY, Observable, of} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {environment} from '../../../environments/environment';
import {Event} from "../model/event";
import {NOT_FOUND, PRECONDITION_FAILED} from "http-status-codes";
import * as EventApiActions from "../actions/event-api.actions";
import {Store} from "@ngrx/store";


@Injectable()
export class EventService {

    constructor(private store: Store<any>, private httpClient: HttpClient) {
    }

    findAll(): Observable<Event[]> {
        return this.httpClient.get<Event[]>(`${environment.zeusServerScenarioApiBaseUri}/events`).pipe(
            tap(events => {
                    this.store.dispatch(EventApiActions.loadedAllSuccessfully({events: events}));
                }
            ),
            catchError(() => {
                this.store.dispatch(EventApiActions.unexpectedError());
                return EMPTY;
            }));
    }

    findById(id: string): Observable<Event> {
        return this.httpClient.get<Event>(`${environment.zeusServerScenarioApiBaseUri}/events/${id}`).pipe(
            tap(event => {
                    this.store.dispatch(EventApiActions.refresh({event: event}));
                }
            ),
            catchError((error: HttpErrorResponse) => {
                if (error.status === NOT_FOUND) {
                    this.store.dispatch(EventApiActions.notFound({id: id}));
                    return of(error.error);
                }
                this.store.dispatch(EventApiActions.unexpectedError());
                return EMPTY;
            }));
    }

    save(event: Event): Observable<Event> {
        return this.httpClient.put<Event>(`${environment.zeusServerScenarioApiBaseUri}/events/${event.id}`, event,
            {headers: new HttpHeaders().set('If-Match', `${event.version}`)}).pipe(
            tap(event => {
                    this.store.dispatch(EventApiActions.refresh({event: event}));
                    this.store.dispatch(EventApiActions.savedSuccessfully({event: event}));
                }
            ),
            catchError((error: HttpErrorResponse) => {
                if (error.status === PRECONDITION_FAILED) {
                    this.store.dispatch(EventApiActions.refresh({event: error.error}));
                    this.store.dispatch(EventApiActions.concurrentModification({event: error.error}));
                    return of(error.error);
                }
                this.store.dispatch(EventApiActions.unexpectedError());
                return EMPTY;
            }));
    }

    fire(event: Event): Observable<Event> {
        return this.httpClient.post<Event>(`${environment.zeusServerScenarioApiBaseUri}/events/${event.id}!fire`, {}).pipe(
            tap(event => {
                    this.store.dispatch(EventApiActions.refresh({event: event}));
                    this.store.dispatch(EventApiActions.firedSuccessfully({event: event}));
                }
            ),
            catchError(() => {
                this.store.dispatch(EventApiActions.unexpectedError());
                return EMPTY;
            }));
    }
}
