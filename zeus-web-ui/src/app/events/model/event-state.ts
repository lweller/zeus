import {Event} from './event'
import {createFeatureSelector, createSelector} from "@ngrx/store";

export interface EventState {
    events: Event[];
    editedEvent: Event;
}

export const initialEventState: EventState = {
    events: null,
    editedEvent: null
};

export const EVENT_STATE_ID = 'ch.wellernet.zeus.events';

const eventState = createFeatureSelector<EventState>(EVENT_STATE_ID);

export const events = createSelector(eventState, (state: EventState) => state.events);
export const editedEvent = createSelector(eventState, (state: EventState) => state.editedEvent);