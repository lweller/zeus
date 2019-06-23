import {Inject, Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import * as EventUiActions from "../actions/event-ui.actions";
import {switchMap} from "rxjs/operators";
import {Store} from "@ngrx/store";
import {Router} from "@angular/router";
import {CONFIG, EventsModuleConfig} from "../events.module.config";

@Injectable()
export class EventRoutingEffects {
    // noinspection JSUnusedGlobalSymbols
    edit = createEffect(
        () => this.actions.pipe(
            ofType(EventUiActions.edit),
            switchMap(() => this.router.navigate([this.config.root, 'edit'], {skipLocationChange: true}))
        ), {
            dispatch: false
        }
    );

    constructor(
        private store: Store<any>,
        private actions: Actions,
        private router: Router,
        @Inject(CONFIG) private config: EventsModuleConfig
    ) {
    }
}