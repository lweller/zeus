import {async, ComponentFixture, fakeAsync, inject, TestBed, tick} from '@angular/core/testing';

import {MessageBoxComponent} from './message-box.component';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {TranslateMockService} from '../../../../test/mock/translate.service.mock';
import {TranslateService} from '@ngx-translate/core';
import {Level, Message, State} from '../../model/message';
import {Store} from '@ngrx/store';
import {initialState} from '../../model/message-state';

describe('MessageBoxComponent', () => {
    let fixture: ComponentFixture<MessageBoxComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [MessageBoxComponent],
            imports: [NoopAnimationsModule],
            providers: [provideMockStore({initialState: {messageState: initialState}}), {
                provide: TranslateService,
                useClass: TranslateMockService
            }]
        }).compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(MessageBoxComponent);
        fixture.detectChanges();
    });

    afterEach(() => {
        fixture.destroy();
    });

    it('should initialize component correctly', fakeAsync(
        () => {
            expect(fixture.componentInstance).toBeTruthy();
            expect(fixture.componentInstance.state).toBe(State.ACKNOWLEDGED);
            expect(window.getComputedStyle(fixture.debugElement.nativeElement
                .querySelector('.message-box')).opacity).toBe('0');
        })
    );

    it('should call display when message is reduced to store', fakeAsync(
        inject([Store], (store: MockStore<any>) => {
            // given
            spyOn(fixture.componentInstance, 'display').and.stub();
            const text = 'Test text!';
            const level = Level.INFO;
            const message = {level: level, text: text};

            // when
            store.setState({messageState: {message: message}});

            // then
            expect(fixture.componentInstance.display).toHaveBeenCalledWith(message);
        })
    ));

    it('should display info message and then fade it out after auto acknowledge',
        fakeAsync(() => {
            callDisplayAndVerifyThatMessageHasBeenDisplayed(fixture, Level.INFO);
        }));

    it('should display warning message and then fade it out after auto acknowledge',
        fakeAsync(() => {
            callDisplayAndVerifyThatMessageHasBeenDisplayed(fixture, Level.WARNING);
        }));

    it('should display error message and then fade it out after auto acknowledge',
        fakeAsync(() => {
            callDisplayAndVerifyThatMessageHasBeenDisplayed(fixture, Level.ERROR);
        }));

    it('should acknowledge and fade out message on close',
        fakeAsync(() => {
            // given a displayed message
            fixture.componentInstance.display(<Message>{level: Level.INFO, text: 'Test Message!'});
            fixture.detectChanges();
            expect(window.getComputedStyle(fixture.debugElement.nativeElement
                .querySelector('.message-box')).opacity).toBe('1');

            // when
            fixture.debugElement.nativeElement.querySelector('#close-button').click();
            fixture.detectChanges();

            // then
            verifyThatMessageHasBeenAcknowledged(fixture);
        }));

    it('should not fade out message if a second message is displayed',
        fakeAsync(() => {
            // given a displayed message
            fixture.componentInstance.display(<Message>{level: Level.INFO, text: 'Test Message!'});
            fixture.detectChanges();
            expect(window.getComputedStyle(fixture.debugElement.nativeElement
                .querySelector('.message-box')).opacity).toBe('1');

            // when a second message is display after less than 5 seconds
            tick(4900);
            fixture.componentInstance.display(<Message>{level: Level.ERROR, text: 'Second Test Message!'});
            fixture.detectChanges();
            tick(4900);

            // then second message shall still be visible after another 4.9 seconds
            expect(window.getComputedStyle(fixture.debugElement.nativeElement
                .querySelector('.message-box')).opacity).toBe('1');

            // when
            tick(100);
            fixture.detectChanges();

            // then
            verifyThatMessageHasBeenAcknowledged(fixture);
        }));

    it('should not display message with undefined level',
        fakeAsync(() => {
            // given
            const message = <Message>{level: undefined, text: 'Test text!'};

            // when
            fixture.componentInstance.display(message);
            fixture.detectChanges();

            // then
            expect(window.getComputedStyle(fixture.debugElement.nativeElement
                .querySelector('.message-box')).opacity).toBe('0');
        }));

    it('should not display message with undefined text',
        fakeAsync(() => {
            // given
            const message = <Message>{level: Level.INFO, text: undefined};

            // when
            fixture.componentInstance.display(message);
            fixture.detectChanges();

            // then
            expect(window.getComputedStyle(fixture.debugElement.nativeElement
                .querySelector('.message-box')).opacity).toBe('0');
        }));

    it('should not display message with empty text',
        fakeAsync(() => {
            // given
            const message = <Message>{level: Level.INFO, text: ''};

            // when
            fixture.componentInstance.display(message);
            fixture.detectChanges();

            // then
            expect(window.getComputedStyle(fixture.debugElement.nativeElement
                .querySelector('.message-box')).opacity).toBe('0');
        }));
});


function callDisplayAndVerifyThatMessageHasBeenDisplayed(
    fixture: ComponentFixture<MessageBoxComponent>, level: Level, text = 'Test Message!') {
    // when
    fixture.componentInstance.display(<Message>{level: level, text: text});
    fixture.detectChanges();

    // then
    expect(fixture.componentInstance.state).toBe(State.NEW);
    expect(fixture.debugElement.nativeElement.querySelector('.message-box').innerHTML).toContain(text);
    expect(fixture.debugElement.nativeElement.querySelector('.message-box').classList).toContain(new Map([
        [Level.INFO, 'info'],
        [Level.WARNING, 'warning'],
        [Level.ERROR, 'error'],
    ]).get(level));
    expect(window.getComputedStyle(fixture.debugElement.nativeElement
        .querySelector('.message-box')).opacity).toBe('1');

    // and 4.9 seconds later
    tick(4900);
    fixture.detectChanges();
    expect(window.getComputedStyle(fixture.debugElement.nativeElement
        .querySelector('.message-box')).opacity).toBe('1');

    // and 0.1 seconds later
    tick(100);
    fixture.detectChanges();
    verifyThatMessageHasBeenAcknowledged(fixture);
}

function verifyThatMessageHasBeenAcknowledged(fixture: ComponentFixture<MessageBoxComponent>) {
    // then
    expect(fixture.componentInstance.state).toBe(State.ACKNOWLEDGED);

    // and a few time later
    tick();
    fixture.detectChanges();

    expect(window.getComputedStyle(fixture.debugElement.nativeElement
        .querySelector('.message-box')).opacity).toBe('0');
}

