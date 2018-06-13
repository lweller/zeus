import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpErrorResponse} from '@angular/common/http';
import {PRECONDITION_FAILED} from 'http-status-codes';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/of';
import {tap} from 'rxjs/operators';
import {Device} from '../model/device';
import {environment} from '../../environments/environment';
import {MessageService} from './message.service';
import {TranslateService} from '@ngx-translate/core';

@Injectable()
export class DeviceService {

  constructor(private translateService: TranslateService, private httpClient: HttpClient, private messageService: MessageService) {}


  findAll(): Observable<Device[]> {
    return this.httpClient.get<Device[]>(`${environment.zeusServerDeviceApiBaseUri}/devices`);
  }

  sendCommand(device: Device): Observable<Device> {
    let message;
    this.translateService.get('Command has been successfully executed.')
      .subscribe(result => message = result);
    return this.httpClient.post<Device>(`${environment.zeusServerDeviceApiBaseUri}/devices/${device.id}!sendCommand`, {})
      .pipe(tap(x => this.messageService.displayInfo(message)));
  }

  update(device: Device): Observable<Device> {
    let message;
    return this.httpClient.post<Device>(`${environment.zeusServerDeviceApiBaseUri}/devices/${device.id}!update`, device,
      {headers: new HttpHeaders().set('If-Match', `${device.version}`)})
      .catch((error: HttpErrorResponse) => {
        switch (error.status) {
          case PRECONDITION_FAILED:
            this.translateService.get('Data has not been updated due to concurent modifications.')
              .subscribe(result => message = result);
            this.messageService.displayError(message);
            const reloadedDevice: Device = error.error;
            reloadedDevice.$error = true;
            return Observable.of(reloadedDevice);
          default:
            this.translateService.get('Sorry, an unexpected error happend !')
              .subscribe(result => message = result);
            this.messageService.displayError(message);
            device.$error = true;
            return Observable.of(device);
        }
      })
      .pipe(tap(reloadedDevice => {
        device.$editing = false;
        if (!reloadedDevice.$error) {
          this.translateService.get('The device \'{name}\' has been updated.', {name: device.name})
            .subscribe(result => message = result);
          this.messageService.displayInfo(message);
        }
      }));
  }
}
