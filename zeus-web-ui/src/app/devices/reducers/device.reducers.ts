import {DeviceState, initialDeviceState} from "../model/device-state";
import {Action, createReducer, on} from "@ngrx/store";
import * as DeviceUiActions from "../actions/device-ui.actions";
import * as DeviceApiActions from "../actions/device-api.actions";

const reducer = createReducer(initialDeviceState,
    on(
        DeviceApiActions.loadedAllSuccessfully,
        (state, {devices}) => ({...state, devices: devices})),

    on(
        DeviceUiActions.edit,
        (state, {device}) => ({
            ...state,
            editedDevice: device
        })),

    on(
        DeviceApiActions.savedSuccessfully,
        (state) => ({
            ...state,
            editedDevice: null
        })),

    on(
        DeviceUiActions.modified,
        DeviceApiActions.refresh,
        (state, {device}) => ({
            ...state,
            editedDevice: state.editedDevice && state.editedDevice.id === device.id ? device : state.editedDevice,
            devices: state.devices.map(otherDevice => otherDevice.id === device.id ? device : otherDevice)
        }))
);

export function deviceReducer(state: DeviceState | undefined, action: Action) {
    return reducer(state, action);
}