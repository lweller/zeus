import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {Device} from '../model/device';
import {environment} from '../../environments/environment';


@Injectable()
export class DeviceService {

  constructor(private httpClient: HttpClient) {}

  findAll(): Observable<Device[]> {
    return this.httpClient.get<Device[]>(`${environment.zeusServerApiBaseUri}/devices`);
  }

  sendCommand(device: Device) {
    return this.httpClient.post<Device>(`${environment.zeusServerApiBaseUri}/devices/${device.id}!sendCommand`, {});
  }

  update(device: Device) {
    return this.httpClient.post<Device>(`${environment.zeusServerApiBaseUri}/devices/${device.id}!update`, device);
  }
}
