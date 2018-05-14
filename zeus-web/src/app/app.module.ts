import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';


import {AppComponent} from './app.component';
import {DevicesComponent} from './component/device/devices.component';
import {DeviceService} from './service/device.service';


@NgModule({
  declarations: [
    AppComponent,
    DevicesComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule
  ],
  providers: [DeviceService],
  bootstrap: [AppComponent]
})
export class AppModule {}
