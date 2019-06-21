import {createAction, props} from "@ngrx/store";
import {Event} from "../model/event";
import {createActionType} from "../../common/utils/message.util";
import {Level} from "../../common/model/message";
import {translate} from "../../common/utils/translate.util";

const SOURCE = 'Event API';

export const loadedAllSuccessfully = createAction(
    createActionType(SOURCE, 'LOAD_EVENTS_SUCCESS'),
    props<{ events: Event[] }>());
export const refresh = createAction(
    createActionType(SOURCE, 'REFRESH'),
    props<{ event: Event }>());
export const savedSuccessfully = createAction(
    createActionType(SOURCE, 'SAVED_SUCCESSFULLY', translate("The event '{event.name}' has been updated."), Level.INFO),
    props<{ event: Event }>());
export const firedSuccessfully = createAction(
    createActionType(SOURCE, 'FIRE_SUCCESSFULLY', translate("The event '{event.name}' has successfully been fired."), Level.INFO),
    props<{ event: Event }>());
export const notFound = createAction(
    createActionType(SOURCE, 'NOT_FOUND', translate("The event with the ID '{id}' does not exist."), Level.WARNING),
    props<{ id: string }>());
export const concurrentModification = createAction(
    createActionType(SOURCE, 'CONCURRENT_MODIFICATION', translate("The event '{event.name}' has not been updated due to concurrent modifications."), Level.WARNING),
    props<{ event: Event }>());
export const unexpectedError = createAction(
    createActionType(SOURCE, 'UNEXPECTED_ERROR', translate('Sorry, an unexpected error happened !'), Level.ERROR));
