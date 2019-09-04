import {deviceReducer} from './device.reducers';
import * as DeviceUiActions from '../actions/device-ui.actions';
import * as DeviceApiActions from '../actions/device-api.actions';
import {DeviceState, initialDeviceState} from '../model/device-state';
import * as uuid from 'uuid';

describe('DeviceReducers', () => {
    it('should set devices in state when loadedAllSuccessfully API action is dispatched', () => {
        // given
        const device1 = {
            id: uuid.v4(),
            version: 42,
            name: 'Test Device 1',
            state: 'OFF'
        };
        const device2 = {
            id: uuid.v4(),
            version: 44,
            name: 'Test Device 2',
            state: 'ON'
        };
        const devices = [device1, device2];

        // when
        const state = deviceReducer(initialDeviceState, DeviceApiActions.loadedAllSuccessfully({devices: devices}));

        // then
        expect(state.devices).toBeTruthy();
        expect(state.devices.length).toBe(2);
        expect(state.devices).toContain(device1);
        expect(state.devices).toContain(device2);
    });

    it('should set edited device in state when edit UI action is dispatched', () => {
        // given
        const device = {
            id: uuid.v4(),
            version: 42,
            name: 'Test Device 1',
            state: 'OFF'
        };

        // when
        const state = deviceReducer(initialDeviceState, DeviceUiActions.edit({device: device}));

        // then
        expect(state.editedDevice).toBe(device);
    });

    it('should unset edited device in state when when savedSuccessfully API action is dispatched', () => {
        // given
        const device = {
            id: uuid.v4(),
            version: 42,
            name: 'Test Device 1',
            state: 'OFF'
        };
        // when
        const state = deviceReducer(initialDeviceState, DeviceApiActions.savedSuccessfully({device: device}));

        // then
        expect(state.editedDevice).not.toBeTruthy();
    });

    [
        {description: 'modified UI', action: DeviceUiActions.modified},
        {description: 'refresh API', action: DeviceApiActions.refresh}
    ].forEach((param) => {
        it(`should update modified edited device and device list of state when ${param.description} action is dispatched and devices are loaded and edited device is set`, () => {
            // given
            const device1 = {
                id: uuid.v4(),
                version: 42,
                name: 'Test Device 1',
                state: 'OFF'
            };
            const device1Updated = {
                id: device1.id,
                version: 42,
                name: 'Test Device 1',
                state: 'OFF'
            };
            const device2 = {
                id: uuid.v4(),
                version: 44,
                name: 'Test Device 2',
                state: 'ON'
            };
            const initialState: DeviceState = {devices: [device1, device2], editedDevice: device1};

            // when
            const state = deviceReducer(initialState, param.action({device: device1Updated}));

            // then
            verifyThatStateContainsExactlyThisDevices(state, device1Updated, device2);
            expect(state.editedDevice).toBe(device1Updated);
        });

        it(`should only update modified device in device list when ${param.description} action is dispatched and devices are loaded but edited device not set`, () => {
            // given
            const device1 = {
                id: uuid.v4(),
                version: 42,
                name: 'Test Device 1',
                state: 'OFF'
            };
            const device1Updated = {
                id: device1.id,
                version: 42,
                name: 'Test Device 1',
                state: 'OFF'
            };
            const device2 = {
                id: uuid.v4(),
                version: 44,
                name: 'Test Device 2',
                state: 'ON'
            };
            const initialState: DeviceState = {devices: [device1, device2], editedDevice: undefined};

            // when
            const state = deviceReducer(initialState, param.action({device: device1Updated}));

            // then
            verifyThatStateContainsExactlyThisDevices(state, device1Updated, device2);
        });

        it(`should only update modified edited device in state when ${param.description} action is dispatched and devices are not loaded`, () => {
            // given
            const device1 = {
                id: uuid.v4(),
                version: 42,
                name: 'Test Device 1',
                state: 'OFF'
            };
            const device1Updated = {
                id: device1.id,
                version: 42,
                name: 'Test Device 1',
                state: 'OFF'
            };
            const initialState: DeviceState = {devices: undefined, editedDevice: device1};

            // when
            const state = deviceReducer(initialState, param.action({device: device1Updated}));

            // then
            expect(state.editedDevice).toBe(device1Updated);
        });

        it(`should not update modified edited device in state when ${param.description} action is dispatched and edited device is an other one`, () => {
            // given
            const device1 = {
                id: uuid.v4(),
                version: 42,
                name: 'Test Device 1',
                state: 'OFF'
            };
            const device2 = {
                id: uuid.v4(),
                version: 44,
                name: 'Test Device 2',
                state: 'ON'
            };
            const device1Updated = {
                id: device1.id,
                version: 42,
                name: 'Test Device 1',
                state: 'OFF'
            };
            const initialState: DeviceState = {devices: undefined, editedDevice: device2};

            // when
            const state = deviceReducer(initialState, param.action({device: device1Updated}));

            // then
            expect(state.editedDevice).toBe(device2);
        });
    });
});

function verifyThatStateContainsExactlyThisDevices(state, device1, device2) {
    expect(state.devices).toBeTruthy();
    expect(state.devices.length).toBe(2);
    expect(state.devices).toContain(device1);
    expect(state.devices).toContain(device2);
}
