import {EventState, initialEventState} from "../states/event.state";
import {Action, createReducer, on} from "@ngrx/store";
import * as EventActions from "../actions/event.actions";

const reducer = createReducer(initialEventState,
    on(
        EventActions.loadedAllSuccessfully,
        (state, {events}) => ({...state, events: events})),

    on(
        EventActions.selected,
        (state, {id}) => ({
            ...state,
            selectedEvent: state.events.find(event => event.id === id)
        })),

    on(
        EventActions.modified,
        EventActions.savedSuccessfully,
        EventActions.firedSuccessfully,
        (state, {event}) => ({
            ...state,
            selectedEvent: state.selectedEvent && state.selectedEvent.id === event.id ? event : state.selectedEvent,
            events: state.events.map(otherEvent => otherEvent.id === event.id ? event : otherEvent)
        }))
);

export function eventReducer(state: EventState | undefined, action: Action) {
    return reducer(state, action);
}