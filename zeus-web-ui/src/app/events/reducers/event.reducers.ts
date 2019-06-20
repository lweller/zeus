import {EventState, initialEventState} from "../model/event-state";
import {Action, createReducer, on} from "@ngrx/store";
import * as EventUiActions from "../actions/event-ui.actions";
import * as EventApiActions from "../actions/event-api.actions";

const reducer = createReducer(initialEventState,
    on(
        EventApiActions.loadedAllSuccessfully,
        (state, {events}) => ({...state, events: events})),

    on(
        EventUiActions.selected,
        (state, {id}) => ({
            ...state,
            selectedEvent: state.events.find(event => event.id === id)
        })),

    on(
        EventUiActions.modified,
        EventApiActions.savedSuccessfully,
        EventApiActions.firedSuccessfully,
        (state, {event}) => ({
            ...state,
            selectedEvent: state.selectedEvent && state.selectedEvent.id === event.id ? event : state.selectedEvent,
            events: state.events.map(otherEvent => otherEvent.id === event.id ? event : otherEvent)
        }))
);

export function eventReducer(state: EventState | undefined, action: Action) {
    return reducer(state, action);
}