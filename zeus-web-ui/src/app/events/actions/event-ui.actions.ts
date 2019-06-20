import {createAction, props} from "@ngrx/store";
import {Event} from "../model/event";

export const init = createAction('[Events UI] Init');
export const selected = createAction('[Events UI] Event Selected', props<{ id: string }>());
export const modified = createAction('[Events UI] Event Modified', props<{ event: Event }>());
export const fire = createAction('[Events UI] Event Fired', props<{ event: Event }>());