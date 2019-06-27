import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {ScenarioService} from "../services/scenario.service";
import {concatMap, switchMap, withLatestFrom} from "rxjs/operators";
import {EMPTY, of} from "rxjs";
import * as ScenarioUiActions from "../actions/scenario-ui.actions";
import {select, Store} from "@ngrx/store";
import {scenarios} from "../model/scenario-state";

@Injectable()
export class ScenarioEffects {

    // noinspection JSUnusedGlobalSymbols
    loadAll = createEffect(
        () => this.actions.pipe(
            ofType(ScenarioUiActions.init),
            switchMap(action => of(action).pipe(withLatestFrom(this.store.pipe(select(scenarios))))),
            switchMap(([, scenarios]) => {
                    if (scenarios) {
                        return EMPTY
                    }
                    return this.scenarioService.findAll();
                }
            )
        ),
        {dispatch: false}
    );

    // noinspection JSUnusedGlobalSymbols
    save = createEffect(
        () => this.actions.pipe(
            ofType(ScenarioUiActions.modified),
            concatMap(action => this.scenarioService.save(action.scenario))
        ),
        {dispatch: false});

    // noinspection JSUnusedGlobalSymbols
    toggleEnabling = createEffect(
        () => this.actions.pipe(
            ofType(ScenarioUiActions.toggleEnabling),
            switchMap(action => this.scenarioService.toggleEnabling(action.scenario))
        ),
        {dispatch: false});

    constructor(
        private store: Store<any>,
        private actions: Actions,
        private scenarioService: ScenarioService
    ) {
    }

}