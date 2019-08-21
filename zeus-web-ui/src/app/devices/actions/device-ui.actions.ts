import {createAction, props} from "@ngrx/store";
import {Device} from "../model/device";
import {createActionType} from "../../common/utils/message.util";
import {Command} from "../model/command";

const SOURCE = 'Device UI';

export const init = createAction(createActionType(SOURCE, 'INIT'));
export const edit = createAction(createActionType(SOURCE, 'EDIT'), props<{ device: Device }>());
export const selected = createAction(createActionType(SOURCE, 'SELECTED'), props<{ id: string }>());
export const modified = createAction(createActionType(SOURCE, 'MODIFIED'), props<{ device: Device }>());
export const refresh = createAction(createActionType(SOURCE, 'REFRESH'), props<{ device: Device }>());
export const executeCommand = createAction(createActionType(SOURCE, 'EXECUTE_COMMAND'), props<{ device: Device, command: Command }>());