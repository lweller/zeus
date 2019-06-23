import {createAction, props} from "@ngrx/store";
import {Event} from "../model/event";
import {createActionType} from "../../common/utils/message.util";

const SOURCE = 'Event UI';

export const init = createAction(createActionType(SOURCE, 'INIT'));
export const edit = createAction(createActionType(SOURCE, 'EDIT'), props<{ event: Event }>());
export const selected = createAction(createActionType(SOURCE, 'SELECTED'), props<{ id: string }>());
export const modified = createAction(createActionType(SOURCE, 'MODIFIED'), props<{ event: Event }>());
export const fire = createAction(createActionType(SOURCE, 'FIRED'), props<{ event: Event }>());