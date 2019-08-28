import {Component, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {Level, Message, State} from '../../model/message';
import {select, Store} from '@ngrx/store';
import {message} from '../../model/message-state';
import {take, withLatestFrom} from 'rxjs/operators';
import {interval, of, Subscription} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';

@Component({
    selector: 'app-message-box',
    templateUrl: './message-box.component.html',
    styleUrls: ['./message-box.component.css'],
    animations: [
        trigger('visibility-changed', [
            state('shown', style({opacity: 1})),
            state('hidden', style({opacity: 0})),
            transition('hidden <=> shown', animate('1s 100ms ease-in-out')),
        ])
    ]
})
export class MessageBoxComponent implements OnInit {
    level = Level.INFO;
    text = '';
    state = State.ACKNOWLEDGED;
    autoAcknowledgedTrigger: Subscription;

    constructor(private store: Store<any>, private translateService: TranslateService) {
        this.store.pipe(select(message)).subscribe((actualMessage) => this.display(actualMessage));
    }

    ngOnInit() {
    }

    display(actualMessage: Message) {
        if (!actualMessage || !actualMessage.level || !actualMessage.text || actualMessage.text.length === 0) {
            return;
        }
        if (this.autoAcknowledgedTrigger) {
            this.autoAcknowledgedTrigger.unsubscribe();
        }
        of(actualMessage.level)
            .pipe(withLatestFrom(this.translateService.get(actualMessage.text, actualMessage.params)))
            .subscribe(([level, text]) => {
                this.level = level;
                this.text = text;
                this.state = State.NEW;
                this.autoAcknowledgedTrigger = interval(5000)
                    .pipe(take(1))
                    .subscribe(() => this.acknowledge());
            });
    }

    acknowledge() {
        if (this.autoAcknowledgedTrigger) {
            this.autoAcknowledgedTrigger.unsubscribe();
        }
        this.state = State.ACKNOWLEDGED;
    }

    getVisibility() {
        return this.state === State.NEW ? 'shown' : 'hidden';
    }
}
