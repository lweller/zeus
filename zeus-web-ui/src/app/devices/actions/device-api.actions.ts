import {createAction, props} from "@ngrx/store";
import {Device} from "../model/device";
import {createActionType} from "../../common/utils/message.util";
import {Level} from "../../common/model/message";
import {translate} from "../../common/utils/translate.util";

const SOURCE = 'Device API';

export const loadedAllSuccessfully = createAction(
    createActionType(SOURCE, 'LOAD_DEVICES_SUCCESS'),
    props<{ devices: Device[] }>());
export const refresh = createAction(
    createActionType(SOURCE, 'REFRESH'),
    props<{ device: Device }>());
export const savedSuccessfully = createAction(
    createActionType(SOURCE, 'SAVED_SUCCESSFULLY', translate("The device '{device.name}' has been updated."), Level.INFO),
    props<{ device: Device }>());
export const commandExecutedSuccessfully = createAction(
    createActionType(SOURCE, 'COMMAND_EXECUTED_SUCCESSFULLY', translate("Command has been successfully executed."), Level.INFO));
export const notFound = createAction(
    createActionType(SOURCE, 'NOT_FOUND', translate("The device with the ID '{id}' does not exist."), Level.WARNING),
    props<{ id: string }>());
export const concurrentModification = createAction(
    createActionType(SOURCE, 'CONCURRENT_MODIFICATION', translate("The device '{device.name}' has not been updated due to concurrent modifications."), Level.WARNING),
    props<{ device: Device }>());
export const communicationNotSuccessful = createAction(
    createActionType(SOURCE, 'COMMUNICATION_NOT_POSSIBLE', translate("Could not communicate with device, may be it\'s not reachable."), Level.WARNING));
export const communicationInterrupted = createAction(
    createActionType(SOURCE, 'COMMUNICATION_INTERRUPTED', translate("Command has been sent to device, but ended up with a failure, leaving device possibly in an undefined state."), Level.ERROR));
export const unexpectedError = createAction(
    createActionType(SOURCE, 'UNEXPECTED_ERROR', translate('Sorry, an unexpected error happened !'), Level.ERROR));
