import {async, ComponentFixture, fakeAsync, inject, TestBed, tick} from '@angular/core/testing';
import {NavigationBarComponent} from './navigation-bar.component';
import {RouterTestingModule} from '@angular/router/testing';
import {Component} from '@angular/core';
import {Location} from '@angular/common';

@Component({template: ''})
class DummyComponent {
}

describe('NavigationBarComponent', () => {
    let component: NavigationBarComponent;
    let fixture: ComponentFixture<NavigationBarComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [NavigationBarComponent, DummyComponent],
            imports: [RouterTestingModule.withRoutes([
                {path: 'devices', component: DummyComponent},
                {path: 'events', component: DummyComponent},
                {path: 'scenarios', component: DummyComponent}
            ])]
        }).compileComponents().then();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(NavigationBarComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should route to devices when devices button is clicked',
        fakeAsync(inject([Location], (location: Location) => {
            // given
            const button = fixture.debugElement.nativeElement.querySelector('#devices-button');

            // when
            button.click();
            tick();

            // then
            expect(location.path()).toBe('/devices');
        })));

    it('should route to events when events button is clicked',
        fakeAsync(inject([Location], (location: Location) => {
            // given
            const button = fixture.debugElement.nativeElement.querySelector('#events-button');

            // when
            button.click();
            tick();

            // then
            expect(location.path()).toBe('/events');
        })));

    it('should route to scenarios when scenarios button is clicked',
        fakeAsync(inject([Location], (location: Location) => {
            // given
            const button = fixture.debugElement.nativeElement.querySelector('#scenarios-button');

            // when
            button.click();
            tick();

            // then
            expect(location.path()).toBe('/scenarios');
        })));
});
