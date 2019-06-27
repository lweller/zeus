import {createAction, props} from "@ngrx/store";
import {createActionType} from "../../common/utils/message.util";
import {Level} from "../../common/model/message";
import {translate} from "../../common/utils/translate.util";
import {Scenario} from "../model/scenario";


const SOURCE = 'Scenario API';

export const loadedAllSuccessfully = createAction(
    createActionType(SOURCE, 'LOAD_SCENARIOS_SUCCESS'),
    props<{ scenarios: Scenario[] }>());
export const refresh = createAction(
    createActionType(SOURCE, 'REFRESH'),
    props<{ scenario: Scenario }>());
export const savedSuccessfully = createAction(
    createActionType(SOURCE, 'SAVED_SUCCESSFULLY', translate("The scenario '{scenario.name}' has been updated."), Level.INFO),
    props<{ scenario: Scenario }>());
export const notFound = createAction(
    createActionType(SOURCE, 'NOT_FOUND', translate("The scenario with the ID '{id}' does not exist."), Level.WARNING),
    props<{ id: string }>());
export const concurrentModification = createAction(
    createActionType(SOURCE, 'CONCURRENT_MODIFICATION', translate("The scenario '{scenario.name}' has not been updated due to concurrent modifications."), Level.WARNING),
    props<{ scenario: Scenario }>());
export const unexpectedError = createAction(
    createActionType(SOURCE, 'UNEXPECTED_ERROR', translate('Sorry, an unexpected error happened !'), Level.ERROR));
