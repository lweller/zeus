import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MessageBoxComponent} from './message-box.component';
import {MessageService} from "../../service/message.service";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

describe('MessageBoxComponent', () => {
    let component: MessageBoxComponent;
    let fixture: ComponentFixture<MessageBoxComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [MessageBoxComponent],
            imports: [BrowserAnimationsModule],
            providers: [MessageService]
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
