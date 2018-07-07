import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpErrorResponse} from '@angular/common/http';
import {PRECONDITION_FAILED} from 'http-status-codes';
import {Observable, of} from 'rxjs';
import {tap, catchError} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {Scenario} from '../model/scenario';
import {environment} from '../../environments/environment';
import {MessageService} from './message.service';

@Injectable()
export class ScenarioService {

  constructor(private translateService: TranslateService, private httpClient: HttpClient, private messageService: MessageService) {}

  findAll(): Observable<Scenario[]> {
    return this.httpClient.get<Scenario[]>(`${environment.zeusServerScenarioApiBaseUri}/scenarios`);
  }

  toggleEnabling(scenario: Scenario): Observable<Scenario> {
    let message;
    return this.httpClient.post<Scenario>(`${environment.zeusServerScenarioApiBaseUri}/scenarios/${scenario.id}!toggleEnabling`,
      {headers: new HttpHeaders().set('If-Match', `${scenario.version}`)})
      .pipe(catchError((error: HttpErrorResponse) => {
        switch (error.status) {
          case PRECONDITION_FAILED:
            this.translateService.get('Data has not been updated due to concurent modifications.')
              .subscribe(result => message = result);
            this.messageService.displayWarning(message);
            const reloadedScenario: Scenario = error.error;
            reloadedScenario.$error = true;
            return of(reloadedScenario);
          default:
            this.translateService.get('Sorry, an unexpected error happend !')
              .subscribe(result => message = result);
            this.messageService.displayError(message);
            scenario.$error = true;
            return of(scenario);
        }
      }))
      .pipe(tap(reloadedScenario => {
        scenario.$editing = false;
        if (!reloadedScenario.$error) {
          if (reloadedScenario.enabled) {
            this.translateService.get('The scenario \'{name}\' has been enabled.', {name: scenario.name})
              .subscribe(result => message = result);
          } else {
            this.translateService.get('The scenario \'{name}\' has been disabled.', {name: scenario.name})
              .subscribe(result => message = result);
          }
          this.messageService.displayInfo(message);
        }
      }));
  }
}
