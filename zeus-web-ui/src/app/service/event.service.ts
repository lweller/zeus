import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpErrorResponse} from '@angular/common/http';
import {PRECONDITION_FAILED} from 'http-status-codes';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/of';
import {tap} from 'rxjs/operators';
import {TranslateService} from '@ngx-translate/core';
import {Event} from '../model/event';
import {environment} from '../../environments/environment';
import {MessageService} from './message.service';


@Injectable()
export class EventService {

  constructor(private translateService: TranslateService, private httpClient: HttpClient, private messageService: MessageService) {}

  findAll(): Observable<Event[]> {
    return this.httpClient.get<Event[]>(`${environment.zeusServerScenarioApiBaseUri}/events`);
  }

  fire(event: Event): Observable<Event> {
    let message;
    this.translateService.get('Event has successfully been fired.')
      .subscribe(result => message = result);
    return this.httpClient.post<Event>(`${environment.zeusServerScenarioApiBaseUri}/events/${event.id}!fire`, {})
      .pipe(tap(x => this.messageService.displayInfo(message)));
  }
}
