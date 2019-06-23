import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {EventService} from "../services/event.service";
import {concatMap, switchMap, withLatestFrom} from "rxjs/operators";
import {EMPTY, of} from "rxjs";
import * as EventUiActions from "../actions/event-ui.actions";
import {select, Store} from "@ngrx/store";
import {events} from "../model/event-state";

@Injectable()
export class EventEffects {

    // noinspection JSUnusedGlobalSymbols
    loadAll = createEffect(
        () => this.actions.pipe(
            ofType(EventUiActions.init),
            switchMap(action => of(action).pipe(withLatestFrom(this.store.pipe(select(events))))),
            switchMap(([, events]) => {
                    if (events) {
                        return EMPTY
                    }
                    return this.eventService.findAll();
                }
            )
        ),
        {dispatch: false}
    );

    // noinspection JSUnusedGlobalSymbols
    save = createEffect(
        () => this.actions.pipe(
            ofType(EventUiActions.modified),
            concatMap(action => this.eventService.save(action.event))
        ),
        {dispatch: false});

    // noinspection JSUnusedGlobalSymbols
    fire = createEffect(
        () => this.actions.pipe(
            ofType(EventUiActions.fire),
            switchMap(action => this.eventService.fire(action.event))
        ),
        {dispatch: false});

    constructor(
        private store: Store<any>,
        private actions: Actions,
        private eventService: EventService
    ) {
    }

}