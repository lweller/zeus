import {Device} from './device'
import {createFeatureSelector, createSelector} from "@ngrx/store";

export interface DeviceState {
    devices: Device[];
    editedDevice: Device;
}

export const initialDeviceState: DeviceState = {
    devices: null,
    editedDevice: null
};

export const DEVICE_STATE_ID = 'ch.wellernet.zeus.devices';

const deviceState = createFeatureSelector<DeviceState>(DEVICE_STATE_ID);

export const devices = createSelector(deviceState, (state: DeviceState) => state.devices);
export const editedDevice = createSelector(deviceState, (state: DeviceState) => state.editedDevice);