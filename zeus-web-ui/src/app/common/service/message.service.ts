import {Injectable} from '@angular/core';
import {interval, Observable, of, Subscription} from 'rxjs';
import {take} from 'rxjs/operators';
import {LEVEL_ERROR, LEVEL_INFO, LEVEL_WARNING, Message, STATE_DONE, STATE_NEW} from '../model/message';

@Injectable()
export class MessageService {

    currentMessage = new Message();
    stateTrigger: Subscription;

    constructor() {
    }

    getCurrentMessage(): Observable<Message> {
        return of(this.currentMessage);
    }

    displayInfo(message: string) {
        this.displayMessage(message, LEVEL_INFO);
    }

    displayWarning(message: string) {
        this.displayMessage(message, LEVEL_WARNING);
    }

    displayError(message: string) {
        this.displayMessage(message, LEVEL_ERROR);
    }

    reset() {
        if (this.stateTrigger != null) {
            this.stateTrigger.unsubscribe();
        }
        this.currentMessage.state = STATE_DONE;
    }

    private displayMessage(message: string, level: string) {
        if (this.stateTrigger != null) {
            this.stateTrigger.unsubscribe();
        }
        this.currentMessage.message = message;
        this.currentMessage.level = level;
        this.currentMessage.state = STATE_NEW;
        this.stateTrigger = interval(5000).pipe(take(1)).subscribe(() => this.currentMessage.state = STATE_DONE);
    }
}
