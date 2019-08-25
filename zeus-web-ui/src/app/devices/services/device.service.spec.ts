import {inject, TestBed} from '@angular/core/testing';

import {COMMUNICATION_INTERRUPTED, COMMUNICATION_NOT_SUCCESSFUL, DeviceService} from './device.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {provideMockStore} from '@ngrx/store/testing';
import {Device} from '../model/device';
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
                httpMock.expectOne(request =>
                    request.method === 'GET' &&
                    request.url === 'http://localhost:8080/deviceApi/v1/devices').flush(devices);
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
                httpMock.expectOne(request =>
                    request.method === 'GET' &&
                    request.url === `http://localhost:8080/deviceApi/v1/devices/${deviceId}`).flush(device);
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
                httpMock.expectOne(request =>
                    request.method === 'GET' &&
                    request.url === `http://localhost:8080/deviceApi/v1/devices/${deviceId}`).flush(null,
                    {
                        status: NOT_FOUND,
                        statusText: 'Not Found'
                    });
                expect(storeMock.dispatch).toHaveBeenCalledWith(DeviceApiActions.notFound({id: deviceId}));
            }));

    it('should dispatch refresh and savedSuccessfully action when save is called for existent device',
        inject([DeviceService, HttpTestingController, Store],
            (service: DeviceService, httpMock: HttpTestingController, storeMock: Store<DeviceState>) => {
                // given
                const deviceId = '00000000-0000-0000-000000000002';
                const device: Device = {
                    id: deviceId,
                    version: 42,
                    name: 'Test Device',
                    state: 'ON'
                };

                // when
                service.save(device).subscribe();

                // then
                httpMock.expectOne(request =>
                    request.method === 'POST' &&
                    request.url === `http://localhost:8080/deviceApi/v1/devices`).flush(device);
                expect(storeMock.dispatch).toHaveBeenCalledWith(DeviceApiActions.refresh({device: device}));
                expect(storeMock.dispatch).toHaveBeenCalledWith(DeviceApiActions.savedSuccessfully({device: device}));
            }));

    it('should dispatch refresh and commandExecutedSuccessfully action when executeCommand is called for existent device',
        inject([DeviceService, HttpTestingController, Store],
            (service: DeviceService, httpMock: HttpTestingController, storeMock: Store<DeviceState>) => {
                // given
                const deviceId = '00000000-0000-0000-000000000002';
                const device: Device = {
                    id: deviceId,
                    version: 42,
                    name: 'Test Device',
                    state: 'ON'
                };

                // when
                service.executeCommand(device).subscribe();

                // then
                httpMock.expectOne(request =>
                    request.method === 'POST' &&
                    request.url === `http://localhost:8080/deviceApi/v1/devices/${deviceId}/main-command!execute`).flush(device);
                expect(storeMock.dispatch).toHaveBeenCalledWith(DeviceApiActions.refresh({device: device}));
                expect(storeMock.dispatch).toHaveBeenCalledWith(DeviceApiActions.commandExecutedSuccessfully());
            }));

    it('should dispatch notFound action when executeCommand is called for not existent device',
        inject([DeviceService, HttpTestingController, Store],
            (service: DeviceService, httpMock: HttpTestingController, storeMock: Store<DeviceState>) => {
                // given
                const deviceId = '00000000-0000-0000-000000000002';
                const device: Device = {
                    id: deviceId,
                    version: 42,
                    name: 'Test Device',
                    state: 'ON'
                };
                // when
                service.executeCommand(device).subscribe();

                // then
                httpMock.expectOne(request =>
                    request.method === 'POST' &&
                    request.url === `http://localhost:8080/deviceApi/v1/devices/${deviceId}/main-command!execute`).flush(null,
                    {
                        status: NOT_FOUND,
                        statusText: 'Not Found'
                    });
                expect(storeMock.dispatch).toHaveBeenCalledWith(DeviceApiActions.notFound({id: deviceId}));
            }));

    it('should dispatch refresh and commandExecutedSuccessfully action when executeCommand is called for existent device',
        inject([DeviceService, HttpTestingController, Store],
            (service: DeviceService, httpMock: HttpTestingController, storeMock: Store<DeviceState>) => {
                // given
                const deviceId = '00000000-0000-0000-000000000002';
                const device: Device = {
                    id: deviceId,
                    version: 42,
                    name: 'Test Device',
                    state: 'ON'
                };

                // when
                service.executeCommand(device).subscribe();

                // then
                httpMock.expectOne(request =>
                    request.method === 'POST' &&
                    request.url === `http://localhost:8080/deviceApi/v1/devices/${deviceId}/main-command!execute`)
                    .flush(device, {status: COMMUNICATION_NOT_SUCCESSFUL, statusText: 'Application Error'});
                expect(storeMock.dispatch).toHaveBeenCalledWith(DeviceApiActions.refresh({device: device}));
                expect(storeMock.dispatch).toHaveBeenCalledWith(DeviceApiActions.communicationNotSuccessful());
            }));

    it('should dispatch refresh and commandExecutedSuccessfully action when executeCommand is called for existent device',
        inject([DeviceService, HttpTestingController, Store],
            (service: DeviceService, httpMock: HttpTestingController, storeMock: Store<DeviceState>) => {
                // given
                const deviceId = '00000000-0000-0000-000000000002';
                const device: Device = {
                    id: deviceId,
                    version: 42,
                    name: 'Test Device',
                    state: 'ON'
                };

                // when
                service.executeCommand(device).subscribe();

                // then
                httpMock.expectOne(request =>
                    request.method === 'POST' &&
                    request.url === `http://localhost:8080/deviceApi/v1/devices/${deviceId}/main-command!execute`)
                    .flush(device, {status: COMMUNICATION_INTERRUPTED, statusText: 'Application Error'});
                expect(storeMock.dispatch).toHaveBeenCalledWith(DeviceApiActions.refresh({device: device}));
                expect(storeMock.dispatch).toHaveBeenCalledWith(DeviceApiActions.communicationInterrupted());
            }));
});
