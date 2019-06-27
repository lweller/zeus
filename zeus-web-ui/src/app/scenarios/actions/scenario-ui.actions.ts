import {createAction, props} from "@ngrx/store";
import {Scenario} from "../model/scenario";
import {createActionType} from "../../common/utils/message.util";

const SOURCE = 'Scenario UI';

export const init = createAction(createActionType(SOURCE, 'INIT'));
export const edit = createAction(createActionType(SOURCE, 'EDIT'), props<{ scenario: Scenario }>());
export const selected = createAction(createActionType(SOURCE, 'SELECTED'), props<{ id: string }>());
export const modified = createAction(createActionType(SOURCE, 'MODIFIED'), props<{ scenario: Scenario }>());
export const toggleEnabling = createAction(createActionType(SOURCE, 'TOOGGLE_ENABLING'), props<{ scenario: Scenario }>());