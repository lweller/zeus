import {inject, TestBed} from '@angular/core/testing';

import {DeviceService} from './device.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {provideMockStore} from '@ngrx/store/testing';
import {DeviceState} from '../model/device-state';
import {Store} from '@ngrx/store';
import * as DeviceApiActions from '../actions/device-api.actions';
import {NOT_FOUND} from 'http-status-codes';

describe('DeviceService', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [DeviceService, provideMockStore()]
        });

        spyOn(TestBed.get(Store), 'dispatch').and.stub();
    });

    afterEach(inject([HttpTestingController], (httpMock: HttpTestingController) => {
        httpMock.verify();
    }));

    it('should dispatch loadedAllSuccessfully action when findAll is called',
        inject([DeviceService, HttpTestingController, Store],
            (service: DeviceService, httpMock: HttpTestingController, storeMock: Store<DeviceState>) => {
                // given
                const devices = [{
                    id: '00000000-0000-0000-000000000002',
                    name: 'Test Device',
                    state: 'ON',
                    version: 42
                }];

                // when
                service.findAll().subscribe();

                // then
                httpMock.expectOne('http://localhost:8080/deviceApi/v1/devices').flush(devices);
                expect(storeMock.dispatch).toHaveBeenCalledWith(DeviceApiActions.loadedAllSuccessfully({devices: devices}));
            }));


    it('should dispatch refresh action when findById is called for existent device',
        inject([DeviceService, HttpTestingController, Store],
            (service: DeviceService, httpMock: HttpTestingController, storeMock: Store<DeviceState>) => {
                // given
                const deviceId = '00000000-0000-0000-000000000002';
                const device = {
                    id: deviceId,
                    name: 'Test Device',
                    state: 'ON',
                    version: 42
                };

                // when
                service.findById(deviceId).subscribe();

                // then
                httpMock.expectOne(`http://localhost:8080/deviceApi/v1/devices/${deviceId}`).flush(device);
                expect(storeMock.dispatch).toHaveBeenCalledWith(DeviceApiActions.refresh({device: device}));
            }));

    it('should dispatch notFound action when findById is called for not existent device',
        inject([DeviceService, HttpTestingController, Store],
            (service: DeviceService, httpMock: HttpTestingController, storeMock: Store<DeviceState>) => {
                // given
                const deviceId = '00000000-0000-0000-000000000002';

                // when
                service.findById(deviceId).subscribe();

                // then
                httpMock.expectOne(`http://localhost:8080/deviceApi/v1/devices/${deviceId}`).flush(null,
                    {
                        status: NOT_FOUND,
                        statusText: 'Not Found'
                    });
                expect(storeMock.dispatch).toHaveBeenCalledWith(DeviceApiActions.notFound({id: deviceId}));
            }));
});
