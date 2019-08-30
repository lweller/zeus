import {DebugElement} from '@angular/core';

export enum FocusEvent {
    BLUR = 'blur'
}

export function fireFocusEvent(element: DebugElement, eventType: FocusEvent) {
    const event = document.createEvent('FocusEvent');
    event.initEvent(
        eventType.toString(),
        true,
        undefined);
    element.nativeElement.dispatchEvent(event);
}
