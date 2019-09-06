import {DebugElement} from '@angular/core';

export function fireInputEvent(element: DebugElement) {
    const event = new Event('input', {bubbles: true, cancelable: undefined});
    element.nativeElement.dispatchEvent(event);
}
