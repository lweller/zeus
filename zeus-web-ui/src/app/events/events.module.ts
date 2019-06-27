import {ModuleWithProviders, NgModule} from '@angular/core';
import {EventsComponent} from "./components/events.component";
import {EventEditComponent} from "./components/event-edit.component";
import {StoreModule} from "@ngrx/store";
import {EVENT_STATE_ID} from "./model/event-state";
import {eventReducer} from "./reducers/event.reducers";
import {EffectsModule} from "@ngrx/effects";
import {EventEffects} from "./effects/event.effects";
import {EventService} from "./services/event.service";
import {FlexLayoutModule} from "@angular/flex-layout";
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {ZeusCommonModule} from "../common/common.module";
import {EventsRoutingModule} from "./events-routing.module";
import {EventRoutingEffects} from "./effects/event-routing.effects";
import {CONFIG, EventsModuleConfig} from "./events.module.config";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {MatMenuModule} from "@angular/material/menu";

@NgModule({
    imports: [
        ZeusCommonModule,
        HttpClientModule,
        FlexLayoutModule,
        FormsModule,
        MatInputModule, MatButtonModule, MatMenuModule,
        EventsRoutingModule,
        StoreModule.forFeature(EVENT_STATE_ID, eventReducer),
        EffectsModule.forFeature([EventEffects, EventRoutingEffects])
    ],
    declarations: [
        EventsComponent,
        EventEditComponent
    ],
    providers: [
        EventService
    ]
})
export class EventsModule {

    static with(config: EventsModuleConfig): ModuleWithProviders {
        return {
            ngModule: EventsModule,
            providers: [
                {
                    provide: CONFIG,
                    useValue: config
                }
            ]
        }
    }

}
