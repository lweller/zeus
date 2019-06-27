import {TranslateService} from '@ngx-translate/core';
import {Component, OnInit} from '@angular/core';
import {Event} from '../model/event';
import {ActivatedRoute, Router} from "@angular/router";
import {select, Store} from "@ngrx/store";
import * as EventActions from "../actions/event-ui.actions";
import {events} from "../model/event-state";
import {cloneDeep} from 'lodash';

@Component({
    selector: 'app-events',
    templateUrl: './events.component.html',
    styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {

    events: Event[];

    constructor(private store: Store<any>,
                private router: Router,
                private route: ActivatedRoute,
                private translateService: TranslateService) {
        store.pipe(select(events)).subscribe(events => this.events = cloneDeep(events));
    }

    ngOnInit() {
        this.store.dispatch(EventActions.init());
    }

    edit(event: Event) {
        this.store.dispatch(EventActions.edit({event: cloneDeep(event)}))
    }

    save(event: Event): void {
        this.store.dispatch(EventActions.modified({event: cloneDeep(event)}));
    }

    fire(event: Event): void {
        this.store.dispatch(EventActions.fire({event: cloneDeep(event)}));
    }

    buildNextOccurrenceExpression(event: Event): string {
        const date = event.nextScheduledExecution;
        const secondsUntilNextFiring = (new Date(date).getTime() - new Date().getTime()) / 1000;
        let expression = '';
        let fragment = '';
        if (secondsUntilNextFiring < 60) {
            this.translateService.get('less than a minute')
                .subscribe(result => fragment = result);
            expression = fragment;
        } else if (secondsUntilNextFiring > 604800) {
            this.translateService.get('more than a week')
                .subscribe(result => fragment = result);
            expression = fragment;
        } else {
            const days = Math.floor(secondsUntilNextFiring / 86400);
            const hours = Math.floor((secondsUntilNextFiring % 86400) / 3600);
            const minutes = Math.floor((secondsUntilNextFiring % 3600) / 60);
            this.translateService.get('and')
                .subscribe(result => fragment = result);
            const andFragment = fragment;
            if (days > 1) {
                this.translateService.get('{days} days', {days: days})
                    .subscribe(result => fragment = result);
                expression += fragment;
            } else if (days === 1) {
                this.translateService.get('one day')
                    .subscribe(result => fragment = result);
                expression = fragment;
            }
            if (days > 0) {
                expression += minutes > 0 ? ' ' : ' ' + andFragment + ' ';
            }
            if (hours > 1) {
                this.translateService.get('{hours} hours', {hours: hours})
                    .subscribe(result => fragment = result);
                expression += fragment;
            } else if (hours === 1) {
                this.translateService.get('1 hour')
                    .subscribe(result => fragment = result);
                expression += fragment;
            }
            if (days > 0 || hours > 0) {
                expression += ' ' + andFragment + ' ';
            }
            if (minutes > 1) {
                this.translateService.get('{minutes} minutes', {minutes: minutes})
                    .subscribe(result => fragment = result);
                expression += fragment;
            } else if (minutes === 1) {
                this.translateService.get('1 minute')
                    .subscribe(result => fragment = result);
                expression += fragment;
            }
        }
        return expression;
    }
}
