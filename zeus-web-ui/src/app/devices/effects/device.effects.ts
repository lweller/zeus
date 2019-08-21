import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {DeviceService} from "../services/device.service";
import {concatMap, switchMap, withLatestFrom} from "rxjs/operators";
import {EMPTY, of} from "rxjs";
import * as DeviceUiActions from "../actions/device-ui.actions";
import {select, Store} from "@ngrx/store";
import {devices} from "../model/device-state";

@Injectable()
export class DeviceEffects {

    // noinspection JSUnusedGlobalSymbols
    loadAll = createEffect(
        () => this.actions.pipe(
            ofType(DeviceUiActions.init),
            switchMap(action => of(action).pipe(withLatestFrom(this.store.pipe(select(devices))))),
            switchMap(([, devices]) => {
                    if (devices) {
                        return EMPTY
                    }
                    return this.deviceService.findAll();
                }
            )
        ),
        {dispatch: false}
    );

    // noinspection JSUnusedGlobalSymbols
    save = createEffect(
        () => this.actions.pipe(
            ofType(DeviceUiActions.modified),
            concatMap(action => this.deviceService.save(action.device))
        ),
        {dispatch: false});


    // noinspection JSUnusedGlobalSymbols
    refresh = createEffect(
        () => this.actions.pipe(
            ofType(DeviceUiActions.refresh),
            concatMap(action => this.deviceService.refreshState(action.device))
        ),
        {dispatch: false});

    // noinspection JSUnusedGlobalSymbols
    executeCommand = createEffect(
        () => this.actions.pipe(
            ofType(DeviceUiActions.executeCommand),
            concatMap(action => this.deviceService.executeCommand(action.device, action.command))
        ),
        {dispatch: false});

    constructor(
        private store: Store<any>,
        private actions: Actions,
        private deviceService: DeviceService
    ) {
    }

}