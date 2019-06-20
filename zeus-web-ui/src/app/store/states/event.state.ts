import {Event} from '../../model/event'
import {createFeatureSelector, createSelector} from "@ngrx/store";

export interface EventState {
    events: Event[];
    selectedEvent: Event;
}

export const initialEventState: EventState = {
    events: null,
    selectedEvent: null
};

export const EVENT_STATE = 'eventState';

const eventState = createFeatureSelector<EventState>(EVENT_STATE);

export const events = createSelector(eventState, (state: EventState) => state.events);
export const selectedEvent = createSelector(eventState, (state: EventState) => state.selectedEvent);