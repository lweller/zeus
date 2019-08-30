import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {EditableLabelComponent} from './editable-label.component';
import {fireMouseEvent, MouseEvent} from '../../../../test/utils/MouseEventUtils';
import {By} from '@angular/platform-browser';
import {fireKeyboardEvent, Key, KeyboardEvent} from '../../../../test/utils/KeyboardEventUtils';
import {fireFocusEvent, FocusEvent} from '../../../../test/utils/FocusEventUtils';

describe('EditableLabelComponent', () => {
    let fixture: ComponentFixture<EditableLabelComponent>;

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [EditableLabelComponent]
        }).compileComponents().then();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(EditableLabelComponent);
        fixture.detectChanges();
    });

    afterEach(() => {
        fixture.destroy();
    });

    it('should initialize component without value', () => {
        // given nothing special

        // when
        fixture.detectChanges();

        // then
        expect(fixture.componentInstance.editing).toBe(false);
        expect(fixture.debugElement.query(By.css('input')).nativeElement.value).toBe('');
        expect(fixture.debugElement.query(By.css('input')).nativeElement.classList).not.toContain('editing');
        expect(fixture.debugElement.query(By.css('input')).nativeElement.readOnly).toBe(true);

    });

    it('should initialize component with value', () => {
        // given
        const value = 'Test!';

        // when
        fixture.componentInstance.value = value;
        fixture.detectChanges();

        // then
        verifyNotEditing(fixture, value);
    });

    it('should get into edit mode when double clicked',
        fakeAsync(() => {
            // given nothing special
            const value = 'Test!';
            fixture.componentInstance.value = value;
            fixture.detectChanges();
            spyOn(fixture.debugElement.query(By.css('input')).nativeElement, 'setSelectionRange');
            spyOn(fixture.componentInstance.startedEditing, 'emit');

            // when
            fireMouseEvent(fixture.debugElement.query(By.css('input')), MouseEvent.DOUBLE_CLICK);
            tick();
            fixture.detectChanges();

            // then
            expect(fixture.componentInstance.startedEditing.emit).toHaveBeenCalled();
            expect(fixture.componentInstance.editing).toBe(true);
            expect(fixture.debugElement.query(By.css('input')).nativeElement.classList).toContain('editing');
            expect(fixture.debugElement.query(By.css('input')).nativeElement.readOnly).toBe(false);
            expect(fixture.debugElement.query(By.css('input')).nativeElement.setSelectionRange).toHaveBeenCalledWith(0, value.length);
        }));

    it('should cancel editing when esc key is released',
        fakeAsync(() => {
            // given
            const value = 'Test!';
            fixture.componentInstance.value = value;
            fixture.componentInstance.editing = true;
            fixture.detectChanges();
            spyOn(fixture.componentInstance.canceledEditing, 'emit');

            // when
            fixture.debugElement.query(By.css('input')).nativeElement.value = 'Edited!';
            fireKeyboardEvent(fixture.debugElement.query(By.css('input')), KeyboardEvent.KEY_UP, Key.ESC);
            fixture.detectChanges();

            // then
            expect(fixture.componentInstance.canceledEditing.emit).toHaveBeenCalled();
            verifyNotEditing(fixture, value);
        }));

    it('should fire blur event when enter key is released',
        fakeAsync(() => {
            // given
            fixture.componentInstance.value = 'Test!';
            fixture.componentInstance.editing = true;
            fixture.detectChanges();
            spyOn(fixture.debugElement.query(By.css('input')).nativeElement, 'blur');

            // when
            fireKeyboardEvent(fixture.debugElement.query(By.css('input')), KeyboardEvent.KEY_UP, Key.ENTER);
            fixture.detectChanges();

            // then
            expect(fixture.debugElement.query(By.css('input')).nativeElement.blur).toHaveBeenCalled();
        }));

    it('should finish editing when blur event occurs',
        fakeAsync(() => {
            // given
            const value = 'Test!';
            const newValue = 'Test Edited!';
            fixture.componentInstance.value = value;
            fixture.componentInstance.editing = true;
            fixture.detectChanges();
            spyOn(fixture.componentInstance.finishedEditing, 'emit');

            // when
            fixture.debugElement.query(By.css('input')).nativeElement.value = newValue;
            fireFocusEvent(fixture.debugElement.query(By.css('input')), FocusEvent.BLUR);
            fixture.detectChanges();

            // then
            expect(fixture.componentInstance.finishedEditing.emit).toHaveBeenCalledWith(newValue);
            verifyNotEditing(fixture, newValue);
        }));
});

function verifyNotEditing(fixture: ComponentFixture<EditableLabelComponent>, value: string) {
    expect(fixture.componentInstance.editing).toBe(false);
    expect(fixture.componentInstance.value).toBe(value);
    expect(fixture.debugElement.query(By.css('input')).nativeElement.value).toBe(value);
    expect(fixture.debugElement.query(By.css('input')).nativeElement.classList).not.toContain('editing');
    expect(fixture.debugElement.query(By.css('input')).nativeElement.readOnly).toBe(true);
}
