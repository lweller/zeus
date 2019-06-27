import {ModuleWithProviders, NgModule} from '@angular/core';
import {StoreModule} from "@ngrx/store";
import {EffectsModule} from "@ngrx/effects";
import {FlexLayoutModule} from "@angular/flex-layout";
import {FormsModule} from "@angular/forms";
import {HttpClientModule} from "@angular/common/http";
import {ZeusCommonModule} from "../common/common.module";
import {CONFIG, ScenariosModuleConfig} from "./scenarios.module.config";
import {ScenariosRoutingModule} from "./scenarios-routing.module";
import {ScenariosComponent} from "./components/scenarios.component";
import {ScenarioService} from "./services/scenario.service";
import {scenarioReducer} from "./reducers/scenario.reducers";
import {ScenarioEffects} from "./effects/scenario.effects";
import {ScenarioRoutingEffects} from "./effects/scenario-routing.effects";
import {SCENARIO_STATE_ID} from "./model/scenario-state";
import {ScenarioEditComponent} from "./components/scenario-edit.component";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {MatMenuModule} from "@angular/material/menu";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";


@NgModule({
    imports: [
        ZeusCommonModule,
        HttpClientModule,
        FlexLayoutModule,
        FormsModule,
        MatInputModule, MatButtonModule, MatMenuModule, MatSlideToggleModule,
        ScenariosRoutingModule,
        StoreModule.forFeature(SCENARIO_STATE_ID, scenarioReducer),
        EffectsModule.forFeature([ScenarioEffects, ScenarioRoutingEffects])
    ],
    declarations: [
        ScenariosComponent,
        ScenarioEditComponent
    ],
    providers: [
        ScenarioService
    ]
})
export class ScenariosModule {

    static with(config: ScenariosModuleConfig): ModuleWithProviders {
        return {
            ngModule: ScenariosModule,
            providers: [
                {
                    provide: CONFIG,
                    useValue: config
                }
            ]
        }
    }

}
