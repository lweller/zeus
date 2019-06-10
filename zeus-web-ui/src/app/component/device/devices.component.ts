import {Component, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {Device} from '../../model/device';
import {DeviceService} from '../../service/device.service';

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

    constructor(private deviceService: DeviceService) {
    }

    ngOnInit() {
        this.load();
    }

    load(): void {
        this.deviceService.findAll().subscribe(devices => this.devices = devices);
    }

    executeMainCommand(device: Device) {
        this.deviceService.executeCommand(device).subscribe(updatedDevice =>
            Object.assign(this.devices[this.devices.indexOf(device)], updatedDevice));
    }

    update(device: Device) {
        this.deviceService.update(device).subscribe(updatedDevice =>
            Object.assign(this.devices[this.devices.indexOf(device)], updatedDevice));
    }

    refresh(device: Device) {
        this.deviceService.refresh(device).subscribe(updatedDevice =>
            Object.assign(this.devices[this.devices.indexOf(device)], updatedDevice));
    }

    getState(device: Device): string {
        return device.state === 'ON' ? 'enabled' : 'disabled';
    }

}
