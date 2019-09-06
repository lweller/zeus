import {ModuleWithProviders, NgModule} from '@angular/core';
import {DevicesComponent} from './components/devices.component';
import {DeviceEditComponent} from './components/device-edit.component';
import {StoreModule} from '@ngrx/store';
import {DEVICE_STATE_ID} from './model/device-state';
import {deviceReducer} from './reducers/device.reducers';
import {EffectsModule} from '@ngrx/effects';
import {DeviceEffects} from './effects/device.effects';
import {DeviceService} from './services/device.service';
import {FlexLayoutModule} from '@angular/flex-layout';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {ZeusCommonModule} from '../common/common.module';
import {DevicesRoutingModule} from './devices-routing.module';
import {DeviceRoutingEffects} from './effects/device-routing.effects';
import {CONFIG, DevicesModuleConfig} from './devices.module.config';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatMenuModule} from '@angular/material/menu';
import {MatSlideToggleModule} from '@angular/material';

@NgModule({
    imports: [
        ZeusCommonModule,
        HttpClientModule,
        FlexLayoutModule,
        FormsModule,
        MatInputModule, MatButtonModule, MatMenuModule, MatSlideToggleModule,
        DevicesRoutingModule,
        StoreModule.forFeature(DEVICE_STATE_ID, deviceReducer),
        EffectsModule.forFeature([DeviceEffects, DeviceRoutingEffects])
    ],
    declarations: [
        DevicesComponent,
        DeviceEditComponent
    ],
    providers: [
        DeviceService
    ]
})
export class DevicesModule {

    static with(config: DevicesModuleConfig): ModuleWithProviders {
        return {
            ngModule: DevicesModule,
            providers: [
                {
                    provide: CONFIG,
                    useValue: config
                }
            ]
        };
    }
}
