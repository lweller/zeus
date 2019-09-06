import {DeviceEditComponent} from './device-edit.component';
import {async, ComponentFixture, fakeAsync, inject, TestBed, tick} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {CUSTOM_ELEMENTS_SCHEMA} from '@angular/core';
import {FormsModule} from '@angular/forms';
import * as uuid from 'uuid';
import {By} from '@angular/platform-browser';
import {fireInputEvent} from '../../../test/utils/InputEventUtils';
import {fireMouseEvent, MouseEvent} from '../../../test/utils/MouseEventUtils';
import {Store} from '@ngrx/store';
import * as DeviceUiActions from '../actions/device-ui.actions';
import {DEVICE_STATE_ID, DeviceState, initialDeviceState} from '../model/device-state';
import {ActivatedRoute, Router} from '@angular/router';
import {cold} from 'jasmine-marbles';
import {fireKeyboardEvent, Key, KeyboardEvent, Modifier} from '../../../test/utils/KeyboardEventUtils';
import {TranslateMockModule} from '../../../test/mock/translate.mock.module';

describe('DeviceEditComponent', () => {
    let fixture: ComponentFixture<DeviceEditComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            imports: [RouterTestingModule, FormsModule, TranslateMockModule],
            declarations: [DeviceEditComponent],
            schemas: [CUSTOM_ELEMENTS_SCHEMA],
            providers: [provideMockStore({initialState: {[DEVICE_STATE_ID]: initialDeviceState}})]
        }).compileComponents().then();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(DeviceEditComponent);
        fixture.detectChanges();
    });

    afterEach(() => {
        fixture.destroy();
    });

    it('should correctly create component', () => {
        expect(fixture.componentInstance).toBeTruthy();
    });

    it('should set device in component correctly when edited device is updated in store', fakeAsync(
        inject([Store], (store: MockStore<any>) => {
            // given
            const device = {
                id: uuid.v4(),
                version: 42,
                name: 'Test Device',
                state: 'OFF'
            };
            const state = {[DEVICE_STATE_ID]: <DeviceState>{devices: undefined, editedDevice: device}};

            // when
            store.setState(state);
            fixture.detectChanges();
            tick();

            // then
            expect(fixture.componentInstance.device).toEqual(device);
        })
    ));

    it('should initialise component correctly when edited device is set', fakeAsync(() => {
            // given
            const device = {
                id: uuid.v4(),
                version: 42,
                name: 'Test Device',
                state: 'OFF'
            };

            // when
            fixture.componentInstance.device = device;
            fixture.detectChanges();
            tick();

            // then
            expect(fixture.debugElement.query(By.css('#name-field')).nativeElement
                .getAttribute('ng-reflect-model')).toBe(device.name);
        })
    );

    it('should update device when name is modified', fakeAsync(() => {
            // given
            fixture.componentInstance.device = {
                id: uuid.v4(),
                version: 42,
                name: 'Test Device',
                state: 'OFF'
            };
            fixture.detectChanges();
            tick();

            // when component is created
            fixture.debugElement.query(By.css('#name-field')).nativeElement.value = 'Updated Device';
            fireInputEvent(fixture.debugElement.query(By.css('#name-field')));
            fixture.detectChanges();
            tick();

            // then
            expect(fixture.componentInstance.device.name).toBe('Updated Device');
        })
    );

    [
        {
            description: 'save button is clicked',
            when: () => {
                fireMouseEvent(fixture.debugElement.query(By.css('#save-button')), MouseEvent.CLICK);
            }
        },
        {
            description: 'ctrl+enter key is released',
            when: () => {
                fireKeyboardEvent(fixture.debugElement.query(By.css('#root')),
                    KeyboardEvent.KEY_UP, Key.ENTER, [Modifier.CTRL]);
            }
        }
    ].forEach((param) => {
        it(`should call save method when ${param.description}`, fakeAsync(() => {
                // given
                const device = {
                    id: uuid.v4(),
                    version: 42,
                    name: 'Test Device',
                    state: 'OFF'
                };
                fixture.componentInstance.device = device;
                fixture.detectChanges();
                tick();
                spyOn(fixture.componentInstance, 'save');

                // when component is created
                param.when();
                fixture.detectChanges();
                tick();

                // then
                expect(fixture.componentInstance.save).toHaveBeenCalledWith(device);
            })
        );
    });

    it('should dispatch save UI action when save save method is called', fakeAsync(
        inject([Store], (store: MockStore<any>) => {
            // given
            const device = {
                id: uuid.v4(),
                version: 42,
                name: 'Test Device',
                state: 'OFF'
            };
            spyOn(store, 'dispatch');

            // when component is created
            fixture.componentInstance.save(device);
            tick();

            // then
            expect(store.dispatch).toHaveBeenCalledWith(DeviceUiActions.modified({device: device}));
        })
    ));

    [
        {
            description: 'cancel button is clicked',
            when: () => {
                fireMouseEvent(fixture.debugElement.query(By.css('#cancel-button')), MouseEvent.CLICK);
            }
        },
        {
            description: 'esc key is released',
            when: () => {
                fireKeyboardEvent(fixture.debugElement.query(By.css('#root')), KeyboardEvent.KEY_UP, Key.ESC);
            }
        }
    ].forEach((param) => {
        it(`should call close method when ${param.description}`, fakeAsync(() => {
                // given
                fixture.componentInstance.device = {
                    id: uuid.v4(),
                    version: 42,
                    name: 'Test Device',
                    state: 'OFF'
                };
                fixture.detectChanges();
                tick();
                spyOn(fixture.componentInstance, 'close');

                // when component is created
                param.when();
                fixture.detectChanges();
                tick();

                // then
                expect(fixture.componentInstance.close).toHaveBeenCalled();
            })
        );
    });

    it('should unset device and navigate to parent when close method is called', fakeAsync(
        inject([Router, ActivatedRoute], (router: Router, route: ActivatedRoute) => {
            // given
            fixture.componentInstance.device = {
                id: uuid.v4(),
                version: 42,
                name: 'Test Device',
                state: 'OFF'
            };
            const routingPromise = cold('').toPromise();
            spyOn(router, 'navigate').and.returnValue(routingPromise);
            spyOn(routingPromise, 'then');

            // when component is created
            fixture.componentInstance.close();
            tick();

            // then
            expect(fixture.componentInstance.device).not.toBeTruthy();
            expect(router.navigate).toHaveBeenCalledWith(['..'], {relativeTo: route});
            expect(routingPromise.then).toHaveBeenCalled();
        })
    ));
});
