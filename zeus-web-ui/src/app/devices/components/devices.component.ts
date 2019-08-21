import {Component, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {Device} from '../model/device';
import {select, Store} from "@ngrx/store";
import * as DeviceActions from "../../devices/actions/device-ui.actions";
import {cloneDeep} from 'lodash';
import {devices} from '../model/device-state';
import {Command} from "../model/command";

@Component({
    selector: 'app-devices',
    templateUrl: './devices.component.html',
    styleUrls: ['./devices.component.css'],
    animations: [
        trigger('state-changed', [
            state('enabled', style({'background-color': '#4782e2', color: 'white'})),
            state('disabled', style({'background-color': '#c0c0c0', color: 'black'})),
            transition('enabled <=> disabled', animate('0.5s'))
        ])
    ]
})
export class DevicesComponent implements OnInit {

    devices: Device[];

    constructor(private store: Store<any>) {
        store.pipe(select(devices)).subscribe(devices => this.devices = cloneDeep(devices));
    }

    ngOnInit() {
        this.store.dispatch(DeviceActions.init());
    }

    edit(device: Device) {
        this.store.dispatch(DeviceActions.edit({device: cloneDeep(device)}))
    }

    save(device: Device): void {
        this.store.dispatch(DeviceActions.modified({device: cloneDeep(device)}));
    }

    refresh(device: Device): void {
        this.store.dispatch(DeviceActions.refresh({device: cloneDeep(device)}));
    }

    executeCommand(device: Device, command: Command = null): void {
        this.store.dispatch(DeviceActions.executeCommand({device: cloneDeep(device), command: command}));
    }

    isEnabled(device: Device): boolean {
        return device.state === 'ON';
    }
}
