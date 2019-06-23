import {Component, OnInit} from '@angular/core';
import {Event} from "../model/event";
import {ActivatedRoute, Router} from "@angular/router";
import * as EventActions from "../actions/event-ui.actions";
import {select, Store} from "@ngrx/store";
import {editedEvent} from "../model/event-state";
import * as lodash from 'lodash';

@Component({
    selector: 'app-event-edit',
    templateUrl: './event-edit.component.html',
    styleUrls: ['./event-edit.component.css']
})
export class EventEditComponent implements OnInit {

    event: Event;

    constructor(private store: Store<any>,
                private router: Router,
                private route: ActivatedRoute) {
        store.pipe(select(editedEvent)).subscribe(event => event == undefined ? this.close() : this.event = lodash.cloneDeep(event));
    }

    ngOnInit() {
    }

    save(event: Event): void {
        this.store.dispatch(EventActions.modified({event: event}));
    }

    close() {
        this.router.navigate(['..'], {relativeTo: this.route}).then()
    }
}
