import {Component, OnInit} from '@angular/core';
import {Event} from "../../model/event";
import {TranslateService} from "@ngx-translate/core";
import {EventService} from "../../service/event.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
    selector: 'app-event-edit',
    templateUrl: './event.edit.component.html',
    styleUrls: ['./event.edit.component.css']
})
export class EventEditComponent implements OnInit {

    event: Event;

    constructor(private router: Router, private route: ActivatedRoute, private translateService: TranslateService, private eventService: EventService) {
    }

    ngOnInit() {
        this.route.params.subscribe(params => {
            this.load(params['id'])
        });
    }

    load(id: String): void {
        this.eventService.findById(id).subscribe(event => this.event = event);
    }

    save(event: Event) {
        this.eventService.save(event).subscribe(event => this.event = event);
        this.close()
    }

    close() {
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['..'], {relativeTo: this.route})
    }
}
