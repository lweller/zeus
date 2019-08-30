import {DebugElement} from '@angular/core';

export enum KeyboardEvent {
    KEY_UP = 'keyup',
    KEY_DOWN = 'keydown'
}

export enum Key {
    ENTER = 'Enter',
    ESC = 'Esc'
}

export function fireKeyboardEvent(element: DebugElement, eventType: KeyboardEvent, key: Key) {
    const event = document.createEvent('KeyboardEvent');
    event.initKeyboardEvent(
        eventType.toString(),
        true,
        undefined,
        undefined,
        key.toString(),
        undefined,
        undefined,
        undefined,
        undefined);
    element.nativeElement.dispatchEvent(event);
}
