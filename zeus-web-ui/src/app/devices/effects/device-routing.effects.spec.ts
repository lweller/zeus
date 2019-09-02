import {inject, TestBed} from '@angular/core/testing';
import {provideMockStore} from '@ngrx/store/testing';
import {DeviceRoutingEffects} from './device-routing.effects';
import {RouterTestingModule} from '@angular/router/testing';
import {provideMockActions} from '@ngrx/effects/testing';
import {Observable} from 'rxjs';
import * as DeviceUiActions from '../actions/device-ui.actions';
import {CONFIG} from '../devices.module.config';
import {Router} from '@angular/router';
import {Action} from '@ngrx/store';
import {cold, hot} from 'jasmine-marbles';

describe('DeviceRoutingEffects', () => {

    let effects: DeviceRoutingEffects;
    let actions: Observable<Action>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [RouterTestingModule],
            providers: [
                DeviceRoutingEffects,
                {
                    provide: CONFIG,
                    useValue: {root: '/'}
                },
                provideMockStore(),
                provideMockActions(() => actions)]
        });

        effects = TestBed.get(DeviceRoutingEffects);
    });

    it('should route to device edit form when edit UI action is dispatched',
        inject([Router], (router: Router) => {
            // given
            spyOn(router, 'navigate');

            // when
            actions = hot('-a', {a: DeviceUiActions.edit({device: undefined})});

            // then
            expect(effects.edit).toBeObservable(cold(''));
            expect(router.navigate).toHaveBeenCalledWith(['/', 'edit'], {skipLocationChange: true});
        }));

    it('should not route when other UI action is dispatched',
        inject([Router], (router: Router) => {
            // given
            spyOn(router, 'navigate');

            // when
            actions = hot('-a', {a: DeviceUiActions.init()});

            // then
            expect(effects.edit).toBeObservable(cold(''));
            expect(router.navigate).not.toHaveBeenCalled();
        }));
});
