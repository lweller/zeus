import {EMPTY, Observable} from 'rxjs';
import {Action, Store} from '@ngrx/store';
import {inject, TestBed} from '@angular/core/testing';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {provideMockActions} from '@ngrx/effects/testing';
import {DeviceService} from '../services/device.service';
import * as DeviceUiActions from '../actions/device-ui.actions';
import {Command} from '../model/command';
import {cold, hot} from 'jasmine-marbles';
import {DeviceEffects} from './device.effects';
import * as uuid from 'uuid';
import {initialDeviceState} from '../model/device-state';
import SpyObj = jasmine.SpyObj;

describe('DeviceEffects', () => {
    let effects: DeviceEffects;
    let actions: Observable<Action>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                DeviceEffects,
                {
                    provide: DeviceService,
                    useValue: jasmine.createSpyObj([
                        'findAll',
                        'refreshState',
                        'executeCommand'
                    ])
                },
                provideMockStore({initialState: {'ch.wellernet.zeus.devices': initialDeviceState}}),
                provideMockActions(() => actions)
            ]
        });

        effects = TestBed.get(DeviceEffects);
    });

    it('should load devices when refresh UI init is dispatched and devices not already loaded',
        inject([DeviceService], (deviceService: SpyObj<DeviceService>) => {
            // given
            const action = DeviceUiActions.init();
            deviceService.findAll.and.returnValue(EMPTY);

            // when
            actions = hot('-a', {a: action});

            // then
            expect(effects.loadAll).toBeObservable(cold(''));
            expect(deviceService.findAll).toHaveBeenCalled();
        }));

    it('should not load devices when refresh UI init is dispatched and devices are already loaded',
        inject([DeviceService, Store], (deviceService: SpyObj<DeviceService>, store: MockStore<any>) => {
            // given
            store.setState({
                'ch.wellernet.zeus.devices': {
                    devices: [],
                    editedDevice: null
                }
            });
            const action = DeviceUiActions.init();

            // when
            actions = hot('-a', {a: action});

            // then
            expect(effects.loadAll).toBeObservable(cold(''));
            expect(deviceService.findAll).not.toHaveBeenCalled();
        }));

    it('should refresh when refresh UI action is dispatched',
        inject([DeviceService], (deviceService: SpyObj<DeviceService>) => {
            // given
            const device = {
                id: uuid.v4(),
                version: 42,
                name: 'Test Device',
                state: 'OFF'
            };
            const action = DeviceUiActions.refresh({device: device});
            deviceService.refreshState.and.returnValue(EMPTY);

            // when
            actions = hot('-a', {a: action});

            // then
            expect(effects.refresh).toBeObservable(cold(''));
            expect(deviceService.refreshState).toHaveBeenCalledWith(device);
        }));

    it('should execute command when executeCommand UI action is dispatched ',
        inject([DeviceService], (deviceService: SpyObj<DeviceService>) => {
            // given
            const device = {
                id: uuid.v4(),
                version: 42,
                name: 'Test Device',
                state: 'OFF'
            };
            const command = Command.GET_SWITCH_STATE;
            const action = DeviceUiActions.executeCommand({device: device, command: command});
            deviceService.executeCommand.and.returnValue(EMPTY);

            // when
            actions = hot('-a', {a: action});

            // then
            expect(effects.executeCommand).toBeObservable(cold(''));
            expect(deviceService.executeCommand).toHaveBeenCalledWith(device, command);
        }));
});
