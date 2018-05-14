import {Injectable} from '@angular/core';

import {HttpClient, HttpHeaders} from '@angular/common/http';

import {Observable} from 'rxjs/Observable';

import {Device} from '../model/device';


const BASE_API_URL = '/api/v1';

@Injectable()
export class DeviceService {

  constructor(private httpClient: HttpClient) {}

  findAll(): Observable<Device[]> {
    return this.httpClient.get<Device[]>(BASE_API_URL + '/devices');
  }

  sendCommand(device: Device) {
    return this.httpClient.post<Device>(BASE_API_URL + '/devices/' + device.id + '!sendCommand', {});
  }
}
