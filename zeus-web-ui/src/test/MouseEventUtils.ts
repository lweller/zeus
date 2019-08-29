import {DebugElement} from '@angular/core';

export enum MouseEvent {
    DOUBLE_CLICK = 'dblclick'
}

export function fireMouseEvent(element: DebugElement, eventType: MouseEvent) {
    const event = document.createEvent('MouseEvent');
    event.initMouseEvent(
        eventType.toString(),
        true,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined);
    element.nativeElement.dispatchEvent(event);
}
