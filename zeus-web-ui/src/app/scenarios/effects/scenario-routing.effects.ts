import {Inject, Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import * as ScenarioUiActions from "../actions/scenario-ui.actions";
import {switchMap} from "rxjs/operators";
import {Store} from "@ngrx/store";
import {Router} from "@angular/router";
import {CONFIG, ScenariosModuleConfig} from "../scenarios.module.config";

@Injectable()
export class ScenarioRoutingEffects {
    // noinspection JSUnusedGlobalSymbols
    edit = createEffect(
        () => this.actions.pipe(
            ofType(ScenarioUiActions.edit),
            switchMap(() => this.router.navigate([this.config.root, 'edit'], {skipLocationChange: true}))
        ), {
            dispatch: false
        }
    );

    constructor(
        private store: Store<any>,
        private actions: Actions,
        private router: Router,
        @Inject(CONFIG) private config: ScenariosModuleConfig
    ) {
    }
}