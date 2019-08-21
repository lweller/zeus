import {Inject, Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import * as DeviceUiActions from "../actions/device-ui.actions";
import {switchMap} from "rxjs/operators";
import {Store} from "@ngrx/store";
import {Router} from "@angular/router";
import {CONFIG, DevicesModuleConfig} from "../devices.module.config";

@Injectable()
export class DeviceRoutingEffects {
    // noinspection JSUnusedGlobalSymbols
    edit = createEffect(
        () => this.actions.pipe(
            ofType(DeviceUiActions.edit),
            switchMap(() => this.router.navigate([this.config.root, 'edit'], {skipLocationChange: true}))
        ), {
            dispatch: false
        }
    );

    constructor(
        private store: Store<any>,
        private actions: Actions,
        private router: Router,
        @Inject(CONFIG) private config: DevicesModuleConfig
    ) {
    }
}