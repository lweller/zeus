import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {NOT_FOUND, PRECONDITION_FAILED} from 'http-status-codes';
import {EMPTY, Observable, of} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {Device} from '../model/device';
import {environment} from '../../../environments/environment';
import {Command} from '../model/command';
import * as DeviceApiActions from "../../devices/actions/device-api.actions";
import {Store} from "@ngrx/store";

const COMMUNICATION_NOT_SUCCESSFUL = 901;
const COMMUNICATION_INTERRUPTED = 902;

@Injectable()
export class DeviceService {

    constructor(private store: Store<any>, private httpClient: HttpClient) {
    }

    findAll(): Observable<Device[]> {
        return this.httpClient.get<Device[]>(`${environment.zeusServerDeviceApiBaseUri}/devices`).pipe(
            tap(devices => {
                    this.store.dispatch(DeviceApiActions.loadedAllSuccessfully({devices: devices}));
                }
            ),
            catchError(() => {
                this.store.dispatch(DeviceApiActions.unexpectedError());
                return EMPTY;
            }));
    }

    findById(id: string): Observable<Device> {
        return this.httpClient.get<Device>(`${environment.zeusServerDeviceApiBaseUri}/devices/${id}`).pipe(
            tap(device => {
                    this.store.dispatch(DeviceApiActions.refresh({device: device}));
                }
            ),
            catchError((error: HttpErrorResponse) => {
                if (error.status === NOT_FOUND) {
                    this.store.dispatch(DeviceApiActions.notFound({id: id}));
                    return of(error.error);
                }
                this.store.dispatch(DeviceApiActions.unexpectedError());
                return EMPTY;
            }));
    }

    save(device: Device): Observable<Device> {
        return this.httpClient.post<Device>(`${environment.zeusServerDeviceApiBaseUri}/devices`, device,
            {headers: new HttpHeaders().set('If-Match', `${device.version}`)}).pipe(
            tap(device => {
                    this.store.dispatch(DeviceApiActions.refresh({device: device}));
                    this.store.dispatch(DeviceApiActions.savedSuccessfully({device: device}));
                }
            ),
            catchError((error: HttpErrorResponse) => {
                if (error.status === PRECONDITION_FAILED) {
                    this.store.dispatch(DeviceApiActions.refresh({device: error.error}));
                    this.store.dispatch(DeviceApiActions.concurrentModification({device: error.error}));
                    return of(error.error);
                }
                this.store.dispatch(DeviceApiActions.unexpectedError());
                return EMPTY;
            }));
    }

    refreshState(device: Device): Observable<Device> {
        return this.executeCommand(device, Command.GET_SWITCH_STATE);
    }

    executeCommand(device: Device, command: Command = null): Observable<Device> {
        const url = `${environment.zeusServerDeviceApiBaseUri}/devices/${device.id}/`
            + (command == null ? 'main-command' : `commands/${command}`) + '!execute';

        return this.httpClient.post<Device>(url, {})
            .pipe(
                tap(device => {
                        this.store.dispatch(DeviceApiActions.refresh({device: device}));
                        this.store.dispatch(DeviceApiActions.commandExecutedSuccessfully());
                    }
                ),
                catchError((error: HttpErrorResponse) => {
                    switch (error.status) {
                        case COMMUNICATION_NOT_SUCCESSFUL:
                            this.store.dispatch(DeviceApiActions.refresh({device: error.error}));
                            this.store.dispatch(DeviceApiActions.communicationNotSuccessful());
                            return of(error.error);
                        case COMMUNICATION_INTERRUPTED:
                            this.store.dispatch(DeviceApiActions.refresh({device: error.error}));
                            this.store.dispatch(DeviceApiActions.communicationInterrupted());
                            return of(error.error);
                        default:
                            this.store.dispatch(DeviceApiActions.unexpectedError());
                            return EMPTY;
                    }
                }));
    }
}
