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


@Injectable()
export class DeviceService {

  constructor(private httpClient: HttpClient, private messageService: MessageService) {}

  findAll(): Observable<Device[]> {
    return this.httpClient.get<Device[]>(`${environment.zeusServerApiBaseUri}/devices`);
  }

  sendCommand(device: Device): Observable<Device> {
    return this.httpClient.post<Device>(`${environment.zeusServerApiBaseUri}/devices/${device.id}!sendCommand`, {})
      .pipe(tap(_ => this.messageService.displayInfo('DEVICE_SERVICE.COMMAND_EXECUTION_SUCCESSFUL')));
  }

  update(device: Device): Observable<Device> {
    return this.httpClient.post<Device>(`${environment.zeusServerApiBaseUri}/devices/${device.id}!update`, device,
      {headers: new HttpHeaders().set('If-Match', `${device.version}`)})
      .catch((error: HttpErrorResponse) => {
        switch (error.status) {
          case PRECONDITION_FAILED:
            this.messageService.displayError('GENERAL_ERRORS.CONCURENT_MODIFICATION');
            const reloadedDevice = error.error;
            reloadedDevice.error = true;
            return Observable.of(reloadedDevice);
          default:
            this.messageService.displayError('GENERAL_ERRORS.UNEXCPECTED');
            device.error = true;
            device.editing = false;
            return Observable.of(device);
        }
      })
      .pipe(tap(reloadedDevice => {
        if (!reloadedDevice.error) {
          this.messageService.displayInfo('DEVICE_SERVICE.UPDATE_SUCCESSFUL');
        }
      }));
  }
}
