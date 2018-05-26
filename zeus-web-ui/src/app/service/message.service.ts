import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Subscription} from 'rxjs/Subscription';
import 'rxjs/add/observable/interval';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/take';
import {Message, STATE_DONE, STATE_NEW, LEVEL_INFO, LEVEL_WARNING, LEVEL_ERROR} from '../model/message';

@Injectable()
export class MessageService {

  currentMessage = new Message();
  stateTrigger: Subscription;

  constructor() {}

  getCurrentMessage(): Observable<Message> {
    return Observable.of(this.currentMessage);
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

  private displayMessage(message: string, level: string) {
    if (this.stateTrigger != null) {
      this.stateTrigger.unsubscribe();
    }
    this.currentMessage.message = message;
    this.currentMessage.level = level;
    this.currentMessage.state = STATE_NEW;
    this.stateTrigger = Observable.interval(5000).take(1).subscribe(_ => this.currentMessage.state = STATE_DONE);
  }
}
