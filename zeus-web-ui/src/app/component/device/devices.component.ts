import {Component, OnInit} from '@angular/core';
import {Device} from '../../model/device';
import {DeviceService} from '../../service/device.service';

@Component({
  selector: 'app-devices',
  templateUrl: './devices.component.html',
  styleUrls: ['./devices.component.css']
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
}
