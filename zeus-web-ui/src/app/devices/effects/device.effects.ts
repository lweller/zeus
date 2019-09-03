import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {DeviceService} from '../services/device.service';
import {concatMap, filter, ignoreElements, switchMap, withLatestFrom} from 'rxjs/operators';
import {of} from 'rxjs';
import * as DeviceUiActions from '../actions/device-ui.actions';
import {select, Store} from '@ngrx/store';
import {devices} from '../model/device-state';

@Injectable()
export class DeviceEffects {

    loadAll = createEffect(
        () => this.actions.pipe(
            ofType(DeviceUiActions.init),
            switchMap(action => of(action).pipe(withLatestFrom(this.store.pipe(select(devices))))),
            filter(([, actualDevices]) => actualDevices === undefined),
            switchMap(() => this.deviceService.findAll()),
            ignoreElements()
        ),
        {dispatch: false}
    );

    save = createEffect(
        () => this.actions.pipe(
            ofType(DeviceUiActions.modified),
            concatMap(action => this.deviceService.save(action.device)),
            ignoreElements()
        ),
        {dispatch: false});


    refresh = createEffect(
        () => this.actions.pipe(
            ofType(DeviceUiActions.refresh),
            concatMap(action => this.deviceService.refreshState(action.device)),
            ignoreElements()
        ),
        {dispatch: false});

    executeCommand = createEffect(
        () => this.actions.pipe(
            ofType(DeviceUiActions.executeCommand),
            concatMap(action => this.deviceService.executeCommand(action.device, action.command)),
            ignoreElements()
        ),
        {dispatch: false});

    constructor(
        private store: Store<any>,
        private actions: Actions,
        private deviceService: DeviceService
    ) {
    }
}
