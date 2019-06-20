import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {EditableLabelComponent} from './editable-label.component';

describe('EditableLabelComponent', () => {
    let component: EditableLabelComponent;
    let fixture: ComponentFixture<EditableLabelComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [EditableLabelComponent]
        })
            .compileComponents().then();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(EditableLabelComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should be created', () => {
        expect(component).toBeTruthy();
    });
});
