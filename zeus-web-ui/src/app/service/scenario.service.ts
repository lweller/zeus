import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpErrorResponse} from '@angular/common/http';
import {PRECONDITION_FAILED} from 'http-status-codes';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/of';
import {tap} from 'rxjs/operators';
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
}
