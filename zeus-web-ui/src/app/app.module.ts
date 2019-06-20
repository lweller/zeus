import {AppRoutingModule} from './app-routing.module';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {DevicesComponent} from './devices/comonents/devices.component';
import {ScenariosComponent} from './scenarios/components/scenarios.component';
import {DeviceService} from './devices/services/device.service';
import {ScenarioService} from './scenarios/services/scenario.service';
import {StoreModule} from "@ngrx/store";
import {ZeusCommonModule} from "./common/common.module";
import {HttpClientModule} from "@angular/common/http";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {EffectsModule} from "@ngrx/effects";

@NgModule({
    declarations: [
        AppComponent,
        DevicesComponent,
        ScenariosComponent
    ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        ZeusCommonModule,
        HttpClientModule,
        StoreModule.forRoot({}, {
            runtimeChecks: {
                strictStateImmutability: true,
                strictActionImmutability: true,
                strictStateSerializability: true,
                strictActionSerializability: true,
            },
        }),
        EffectsModule.forRoot([]),
        AppRoutingModule
    ],
    providers: [
        DeviceService,
        ScenarioService,
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
