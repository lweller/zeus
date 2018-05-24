import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpErrorResponse} from '@angular/common/http';
import {PRECONDITION_FAILED} from 'http-status-codes';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/of';
import {Device} from '../model/device';
import {environment} from '../../environments/environment';


@Injectable()
export class DeviceService {

  constructor(private httpClient: HttpClient) {}

  findAll(): Observable<Device[]> {
    return this.httpClient.get<Device[]>(`${environment.zeusServerApiBaseUri}/devices`);
  }

  sendCommand(device: Device): Observable<Device> {
    return this.httpClient.post<Device>(`${environment.zeusServerApiBaseUri}/devices/${device.id}!sendCommand`, {});
  }

  update(device: Device): Observable<Device> {
    return this.httpClient.post<Device>(`${environment.zeusServerApiBaseUri}/devices/${device.id}!update`, device,
      {headers: new HttpHeaders().set('If-Match', `${device.version}`)}).catch((error: HttpErrorResponse) => {
        switch (error.status) {
          case PRECONDITION_FAILED: return Observable.of(error.error);
          default: device.editing = false; return Observable.of(device);
        }
      });
  }
}
