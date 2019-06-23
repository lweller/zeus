import {EventState, initialEventState} from "../model/event-state";
import {Action, createReducer, on} from "@ngrx/store";
import * as EventUiActions from "../actions/event-ui.actions";
import * as EventApiActions from "../actions/event-api.actions";

const reducer = createReducer(initialEventState,
    on(
        EventApiActions.loadedAllSuccessfully,
        (state, {events}) => ({...state, events: events})),
    on(
        EventUiActions.edit,
        (state, {event}) => ({
            ...state,
            editedEvent: event
        })),

    on(
        EventApiActions.savedSuccessfully,
        (state) => ({
            ...state,
            editedEvent: null
        })),

    on(
        EventUiActions.modified,
        EventApiActions.refresh,
        (state, {event}) => ({
            ...state,
            editedEvent: state.editedEvent && state.editedEvent.id === event.id ? event : state.editedEvent,
            events: state.events.map(otherEvent => otherEvent.id === event.id ? event : otherEvent)
        }))
);

export function eventReducer(state: EventState | undefined, action: Action) {
    return reducer(state, action);
}