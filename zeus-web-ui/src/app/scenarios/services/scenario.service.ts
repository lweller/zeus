import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {NOT_FOUND, PRECONDITION_FAILED} from 'http-status-codes';
import {EMPTY, Observable, of} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {Scenario} from '../model/scenario';
import {environment} from '../../../environments/environment';
import * as ScenarioApiActions from "../../scenarios/actions/scenario-api.actions";
import {Store} from "@ngrx/store";

@Injectable()
export class ScenarioService {

    constructor(private store: Store<any>, private httpClient: HttpClient) {
    }

    findAll(): Observable<Scenario[]> {
        return this.httpClient.get<Scenario[]>(`${environment.zeusServerScenarioApiBaseUri}/scenarios`).pipe(
            tap(scenarios => {
                    this.store.dispatch(ScenarioApiActions.loadedAllSuccessfully({scenarios: scenarios}));
                }
            ),
            catchError(() => {
                this.store.dispatch(ScenarioApiActions.unexpectedError());
                return EMPTY;
            }));
    }

    findById(id: string): Observable<Scenario> {
        return this.httpClient.get<Scenario>(`${environment.zeusServerScenarioApiBaseUri}/scenarios/${id}`).pipe(
            tap(scenario => {
                    this.store.dispatch(ScenarioApiActions.refresh({scenario: scenario}));
                }
            ),
            catchError((error: HttpErrorResponse) => {
                if (error.status === NOT_FOUND) {
                    this.store.dispatch(ScenarioApiActions.notFound({id: id}));
                    return of(error.error);
                }
                this.store.dispatch(ScenarioApiActions.unexpectedError());
                return EMPTY;
            }));
    }

    save(scenario: Scenario): Observable<Scenario> {
        return this.httpClient.put<Scenario>(`${environment.zeusServerScenarioApiBaseUri}/scenarios/${scenario.id}`, scenario,
            {headers: new HttpHeaders().set('If-Match', `${scenario.version}`)}).pipe(
            tap(scenario => {
                    this.store.dispatch(ScenarioApiActions.refresh({scenario: scenario}));
                    this.store.dispatch(ScenarioApiActions.savedSuccessfully({scenario: scenario}));
                }
            ),
            catchError((error: HttpErrorResponse) => {
                if (error.status === PRECONDITION_FAILED) {
                    this.store.dispatch(ScenarioApiActions.refresh({scenario: error.error}));
                    this.store.dispatch(ScenarioApiActions.concurrentModification({scenario: error.error}));
                    return of(error.error);
                }
                this.store.dispatch(ScenarioApiActions.unexpectedError());
                return EMPTY;
            }));
    }

    toggleEnabling(scenario: Scenario): Observable<Scenario> {
        return this.httpClient.post<Scenario>(`${environment.zeusServerScenarioApiBaseUri}/scenarios/${scenario.id}!toggleEnabling`,
            {headers: new HttpHeaders().set('If-Match', `${scenario.version}`)}).pipe(
            tap(scenario => {
                    this.store.dispatch(ScenarioApiActions.refresh({scenario: scenario}));
                }
            ),
            catchError(() => {
                this.store.dispatch(ScenarioApiActions.unexpectedError());
                return EMPTY;
            }));
    }
}
