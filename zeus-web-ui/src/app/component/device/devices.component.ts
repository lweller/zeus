import {Component, OnInit} from '@angular/core';
import {trigger, state, transition, style, animate} from '@angular/animations';
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
      transition('enabled <=> disabled', animate('0.2s'))
    ])
  ]
})
export class DevicesComponent implements OnInit {

  devices: Device[];

  constructor(private deviceService: DeviceService) {}

  ngOnInit() {
    this.load();
  }

  load(): void {
    this.deviceService.findAll().subscribe(devices => this.devices = devices);
  }

  sendCommand(device: Device) {
    this.deviceService.sendCommand(device).subscribe(updatedDevice =>
      this.devices[this.devices.indexOf(device)] = updatedDevice);
  }

  update(device: Device) {
    this.deviceService.update(device).subscribe(updatedDevice =>
      this.devices[this.devices.indexOf(device)] = updatedDevice);
  }

  getState(device: Device): string {
    return device.state === 'ON' ? 'enabled' : 'disabled';
  }

}
