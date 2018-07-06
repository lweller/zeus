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

const COMMUNICATION_NOT_SUCCESSFUL = 901;
const COMMUNICATION_INTERRUPTED = 902;

@Injectable()
export class DeviceService {

  constructor(private translateService: TranslateService, private httpClient: HttpClient, private messageService: MessageService) {}


  findAll(): Observable<Device[]> {
    return this.httpClient.get<Device[]>(`${environment.zeusServerDeviceApiBaseUri}/devices`);
  }

  sendCommand(device: Device): Observable<Device> {
    let message;
    let updatedDevice: Device = null;
    return this.httpClient.post<Device>(`${environment.zeusServerDeviceApiBaseUri}/devices/${device.id}!sendCommand`, {})
      .catch((error: HttpErrorResponse) => {
        switch (error.status) {
          case COMMUNICATION_NOT_SUCCESSFUL:
            this.translateService.get('Could not communicate with device, may be it\'s not reachable.')
              .subscribe(result => message = result);
            this.messageService.displayWarning(message);
            updatedDevice = error.error;
            updatedDevice.$error = true;
            return Observable.of(updatedDevice);
          case COMMUNICATION_INTERRUPTED:
            this.translateService.get('Command has been sent to device, but ened up with a failure, leaving device possibly in an undefined state.')
              .subscribe(result => message = result);
            this.messageService.displayError(message);
            device.$error = true;
            return Observable.of(device);
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
          this.translateService.get('Command has been successfully executed.', {name: device.name})
            .subscribe(result => message = result);
          this.messageService.displayInfo(message);
        }
      }));
  }

  update(device: Device): Observable<Device> {
    let message;
    let updatedDevice: Device = null;
    return this.httpClient.post<Device>(`${environment.zeusServerDeviceApiBaseUri}/devices/${device.id}!update`, device,
      {headers: new HttpHeaders().set('If-Match', `${device.version}`)})
      .catch((error: HttpErrorResponse) => {
        switch (error.status) {
          case PRECONDITION_FAILED:
            this.translateService.get('Data has not been updated due to concurent modifications.')
              .subscribe(result => message = result);
            this.messageService.displayWarning(message);
            updatedDevice = error.error;
            updatedDevice.$error = true;
            return Observable.of(updatedDevice);
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
