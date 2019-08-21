import {Component, OnInit} from '@angular/core';
import {Device} from "../model/device";
import {ActivatedRoute, Router} from "@angular/router";
import * as DeviceActions from "../actions/device-ui.actions";
import {select, Store} from "@ngrx/store";
import {editedDevice} from "../model/device-state";
import * as lodash from 'lodash';

@Component({
    selector: 'app-device-edit',
    templateUrl: './device-edit.component.html',
    styleUrls: ['./device-edit.component.css']
})
export class DeviceEditComponent implements OnInit {

    device: Device;

    constructor(private store: Store<any>,
                private router: Router,
                private route: ActivatedRoute) {
        store.pipe(select(editedDevice)).subscribe(device => device == undefined ? this.close() : this.device = lodash.cloneDeep(device));
    }

    ngOnInit() {
    }

    save(device: Device): void {
        this.store.dispatch(DeviceActions.modified({device: device}));
    }

    close() {
        this.router.navigate(['..'], {relativeTo: this.route}).then()
    }
}
