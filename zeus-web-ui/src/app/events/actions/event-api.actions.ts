import {createAction, props} from "@ngrx/store";
import {Event} from "../model/event";

export const loadedAllSuccessfully = createAction('[Events API] Events Loaded Successfully', props<{ events: Event[] }>());
export const savedSuccessfully = createAction('[Events API] Event Saved Successfully', props<{ event: Event }>());
export const firedSuccessfully = createAction('[Events API] Event Fired Successfully', props<{ event: Event }>());