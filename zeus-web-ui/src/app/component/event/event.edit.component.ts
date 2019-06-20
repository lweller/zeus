import {Component, OnInit} from '@angular/core';
import {Event} from "../../model/event";
import {ActivatedRoute, Router} from "@angular/router";
import * as EventActions from "../../store/actions/event.actions";
import {select, Store} from "@ngrx/store";
import {selectedEvent} from "../../store/states/event.state";
import * as lodash from 'lodash';

@Component({
    selector: 'app-event-edit',
    templateUrl: './event.edit.component.html',
    styleUrls: ['./event.edit.component.css']
})
export class EventEditComponent implements OnInit {

    event: Event;

    constructor(private store: Store<any>,
                private router: Router,
                private route: ActivatedRoute) {
        store.pipe(select(selectedEvent)).subscribe(event => this.event = lodash.cloneDeep(event));
    }

    ngOnInit() {
        this.route.params.subscribe(params => {
            this.store.dispatch(EventActions.selected({id: params['id']}));
        });
    }

    save(event: Event): void {
        this.store.dispatch(EventActions.modified({event: event}));
        this.close()
    }

    close() {
        this.router.navigate(['..'], {relativeTo: this.route}).then()
    }
}
