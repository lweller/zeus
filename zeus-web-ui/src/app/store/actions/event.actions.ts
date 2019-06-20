import {createAction, props} from "@ngrx/store";
import {Event} from "../../model/event";

// UI actions
export const init = createAction('[Events UI] Init');
export const selected = createAction('[Events UI] Event Selected', props<{ id: string }>());
export const modified = createAction('[Events UI] Event Modified', props<{ event: Event }>());
export const fire = createAction('[Events UI] Event Fired', props<{ event: Event }>());

// API actions
export const loadedAllSuccessfully = createAction('[Events API] Events Loaded Successfully', props<{ events: Event[] }>());
export const savedSuccessfully = createAction('[Events API] Event Saved Successfully', props<{ event: Event }>());
export const firedSuccessfully = createAction('[Events API] Event Fired Successfully', props<{ event: Event }>());