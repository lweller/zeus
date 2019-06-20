import {Injectable} from "@angular/core";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {EventService} from "../../service/event.service";
import {catchError, concatMap, map, switchMap, withLatestFrom} from "rxjs/operators";
import {EMPTY, of} from "rxjs";
import * as EventActions from "../actions/event.actions";
import {select, Store} from "@ngrx/store";
import {events} from "../states/event.state";

@Injectable()
export class EventEffects {
    // noinspection JSUnusedGlobalSymbols
    loadAll = createEffect(
        () => this.actions.pipe(
            ofType(EventActions.init),
            switchMap(action => of(action).pipe(withLatestFrom(this.store.pipe(select(events))))),
            switchMap(([, events]) => {
                    if (events) {
                        return EMPTY
                    }
                    return this.eventService.findAll().pipe(
                        map(events => EventActions.loadedAllSuccessfully({events: events})),
                        catchError(() => EMPTY)
                    )
                }
            )
        )
    );

    // noinspection JSUnusedGlobalSymbols
    save = createEffect(
        () => this.actions.pipe(
            ofType(EventActions.modified),
            concatMap(action =>
                this.eventService.save(action.event).pipe(
                    map(event => EventActions.savedSuccessfully({event: event})),
                    catchError(() => EMPTY)
                )
            )
        )
    );

    // noinspection JSUnusedGlobalSymbols
    fire = createEffect(
        () => this.actions.pipe(
            ofType(EventActions.fire),
            concatMap(action =>
                this.eventService.fire(action.event).pipe(
                    map(event => EventActions.firedSuccessfully({event: event})),
                    catchError(() => EMPTY)
                )
            )
        )
    );

    constructor(
        private store: Store<any>,
        private actions: Actions,
        private eventService: EventService
    ) {
    }
}