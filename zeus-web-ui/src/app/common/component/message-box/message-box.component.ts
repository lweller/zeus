import {Component, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {Level, State} from '../../model/message';
import {select, Store} from "@ngrx/store";
import {message} from "../../model/message-state";
import {filter, switchMap, take, withLatestFrom} from "rxjs/operators";
import {interval, of, Subscription} from "rxjs";
import {TranslateService} from "@ngx-translate/core";

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

    constructor(private store: Store<any>,
                private translateService: TranslateService) {
        store.pipe(
            select(message),
            filter(message => message != undefined && message.level != undefined && message.text != undefined && message.text.length > 0),
            switchMap(message =>
                of(message.level).pipe(
                    withLatestFrom(translateService.get(message.text, message.params)))
            ))
            .subscribe(([level, text]) => this.display(level, text));
    }

    ngOnInit() {
    }

    display(level: Level, text: string) {
        if (this.autoAcknowledgedTrigger) {
            this.autoAcknowledgedTrigger.unsubscribe();
        }
        this.level = level;
        this.text = text;
        this.state = State.NEW;
        this.autoAcknowledgedTrigger = interval(5000).pipe(
            take(1))
            .subscribe(() => this.acknowledge());
    }

    acknowledge() {
        this.state = State.ACKNOWLEDGED;
    }

    getVisibility() {
        return this.state === State.NEW ? 'shown' : 'hidden';
    }

}
