import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MessageBoxComponent} from './message-box.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {provideMockStore} from '@ngrx/store/testing';
import {TranslateMockService} from '../../service/translate.service.mock';
import {TranslateService} from '@ngx-translate/core';

describe('MessageBoxComponent', () => {
    let component: MessageBoxComponent;
    let fixture: ComponentFixture<MessageBoxComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [MessageBoxComponent],
            imports: [BrowserAnimationsModule],
            providers: [provideMockStore(), {provide: TranslateService, useClass: TranslateMockService}]
        })
            .compileComponents().then();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(MessageBoxComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should be created', () => {
        expect(component).toBeTruthy();
    });
});
