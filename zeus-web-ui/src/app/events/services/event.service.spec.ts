import {inject, TestBed} from '@angular/core/testing';

import {EventService} from './event.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {provideMockStore} from '@ngrx/store/testing';
import {Event} from '../model/event';
import {EventState} from '../model/event-state';
import {Store} from '@ngrx/store';
import * as ScenarioApiActions from '../actions/event-api.actions';
import {NOT_FOUND} from 'http-status-codes';

describe('EventService', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [EventService, provideMockStore()]
        });

        spyOn(TestBed.get(Store), 'dispatch').and.stub();
    });

    afterEach(inject([HttpTestingController], (httpMock: HttpTestingController) => {
        httpMock.verify();
    }));

    it('should dispatch loadedAllSuccessfully action when findAll is called',
        inject([EventService, HttpTestingController, Store],
            (service: EventService, httpMock: HttpTestingController, storeMock: Store<EventState>) => {
                // given
                const events: Event[] = [{
                    id: '00000000-0000-0000-000000000002',
                    version: 42,
                    name: 'Test Event',
                    nextScheduledExecution: undefined
                }];

                // when
                service.findAll().subscribe();

                // then
                httpMock.expectOne('http://localhost:8080/scenarioApi/v1/events').flush(events);
                expect(storeMock.dispatch).toHaveBeenCalledWith(ScenarioApiActions.loadedAllSuccessfully({events: events}));
            }));

    it('should dispatch refresh action when findById is called for existent event',
        inject([EventService, HttpTestingController, Store],
            (service: EventService, httpMock: HttpTestingController, storeMock: Store<EventState>) => {
                // given
                const eventId = '00000000-0000-0000-000000000002';
                const event: Event = {
                    id: eventId,
                    version: 42,
                    name: 'Test Event',
                    nextScheduledExecution: undefined
                };

                // when
                service.findById(eventId).subscribe();

                // then
                httpMock.expectOne(request =>
                    request.method === 'GET' &&
                    request.url === `http://localhost:8080/scenarioApi/v1/events/${eventId}`).flush(event);
                expect(storeMock.dispatch).toHaveBeenCalledWith(ScenarioApiActions.refresh({event: event}));
            }));

    it('should dispatch notFound action when findById is called for not existent event',
        inject([EventService, HttpTestingController, Store],
            (service: EventService, httpMock: HttpTestingController, storeMock: Store<EventState>) => {
                // given
                const eventId = '00000000-0000-0000-000000000002';

                // when
                service.findById(eventId).subscribe();

                // then
                httpMock.expectOne(request =>
                    request.method === 'GET' &&
                    request.url === `http://localhost:8080/scenarioApi/v1/events/${eventId}`)
                    .flush(null, {status: NOT_FOUND, statusText: 'Not Found'});
                expect(storeMock.dispatch).toHaveBeenCalledWith(ScenarioApiActions.notFound({id: eventId}));
            }));

    it('should dispatch refresh and savedSuccessfully action when save is called for existent event',
        inject([EventService, HttpTestingController, Store],
            (service: EventService, httpMock: HttpTestingController, storeMock: Store<EventState>) => {
                // given
                const eventId = '00000000-0000-0000-000000000002';
                const event: Event = {
                    id: eventId,
                    version: 42,
                    name: 'Test Event',
                    nextScheduledExecution: undefined
                };

                // when
                service.save(event).subscribe();

                // then
                httpMock.expectOne(request =>
                    request.method === 'POST' &&
                    request.url === `http://localhost:8080/scenarioApi/v1/events`).flush(event);
                expect(storeMock.dispatch).toHaveBeenCalledWith(ScenarioApiActions.refresh({event: event}));
                expect(storeMock.dispatch).toHaveBeenCalledWith(ScenarioApiActions.savedSuccessfully({event: event}));
            }));

    it('should dispatch refresh and firedSuccessfully action when fire is called for existent event',
        inject([EventService, HttpTestingController, Store],
            (service: EventService, httpMock: HttpTestingController, storeMock: Store<EventState>) => {
                // given
                const eventId = '00000000-0000-0000-000000000002';
                const event: Event = {
                    id: eventId,
                    version: 42,
                    name: 'Test Event',
                    nextScheduledExecution: undefined
                };

                // when
                service.fire(event).subscribe();

                // then
                httpMock.expectOne(request =>
                    request.method === 'POST' &&
                    request.url === `http://localhost:8080/scenarioApi/v1/events/${eventId}!fire`).flush(event);
                expect(storeMock.dispatch).toHaveBeenCalledWith(ScenarioApiActions.refresh({event: event}));
                expect(storeMock.dispatch).toHaveBeenCalledWith(ScenarioApiActions.firedSuccessfully({event: event}));
            }));

    it('should dispatch notFound action when fire is called for not existent event',
        inject([EventService, HttpTestingController, Store],
            (service: EventService, httpMock: HttpTestingController, storeMock: Store<EventState>) => {
                // given
                const eventId = '00000000-0000-0000-000000000002';
                const event: Event = {
                    id: eventId,
                    version: 42,
                    name: 'Test Event',
                    nextScheduledExecution: undefined
                };

                // when
                service.fire(event).subscribe();

                // then
                httpMock.expectOne(request =>
                    request.method === 'POST' &&
                    request.url === `http://localhost:8080/scenarioApi/v1/events/${eventId}!fire`)
                    .flush(null, {status: NOT_FOUND, statusText: 'Not Found'});
                expect(storeMock.dispatch).toHaveBeenCalledWith(ScenarioApiActions.notFound({id: eventId}));
            }));
});
