import {AppRoutingModule} from './app-routing.module';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {LOCALE_ID, NgModule} from '@angular/core';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {
    MissingTranslationHandler,
    MissingTranslationHandlerParams,
    TranslateDefaultParser,
    TranslateLoader,
    TranslateModule,
    TranslateParser,
    TranslateService
} from '@ngx-translate/core';
import {registerLocaleData} from '@angular/common';
import localeEn from '@angular/common/locales/en';
import localeDe from '@angular/common/locales/de';
import localeFr from '@angular/common/locales/fr';

import {AppComponent} from './app.component';
import {DevicesComponent} from './component/device/devices.component';
import {EventsComponent} from './component/event/events.component';
import {ScenariosComponent} from './component/scenario/scenarios.component';
import {MessageBoxComponent} from './component/message-box/message-box.component';
import {DeviceService} from './service/device.service';
import {EventService} from './service/event.service';
import {ScenarioService} from './service/scenario.service';
import {MessageService} from './service/message.service';
import {NavigationBarComponent} from './component/navigation-bar/navigation-bar.component';
import {EditableLabelComponent} from './component/editable-label/editable-label.component';
import {TranslatePoHttpLoader} from "./translate-po-http-loader";
import {FlexLayoutModule} from "@angular/flex-layout";
import {FormsModule} from "@angular/forms";
import {EventEditComponent} from "./component/event/event.edit.component";
import {
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatIconModule,
    MatInputModule,
    MatMenuModule,
    MatSelectModule,
    MatToolbarModule
} from "@angular/material";
import {StoreModule} from "@ngrx/store";
import {eventReducer} from "./store/reducers/event.reducers";
import {EffectsModule} from "@ngrx/effects";
import {EventEffects} from "./store/effects/event.effects";
import {EVENT_STATE} from "./store/states/event.state";

registerLocaleData(localeEn);
registerLocaleData(localeDe);
registerLocaleData(localeFr);

export class InterpolatedTranslateParser extends TranslateDefaultParser {
    public templateMatcher: RegExp = /{\s?([^{}\s]*)\s?}/g;
}

export class InterpolatedMissingTranslationHandler implements MissingTranslationHandler {
    public handle(params: MissingTranslationHandlerParams) {
        return params.translateService.parser.interpolate(params.key, params.interpolateParams);
        // Workaround until this PR is merged: https://github.com/ocombe/ng2-translate/pull/348
        // return this.parser.interpolate(params.key, params.interpolateParams);
    }
}

export function createTranslateParser() {
    return new InterpolatedTranslateParser();
}

export function createTranslateLoader(http: HttpClient) {
    return new TranslatePoHttpLoader(http, '/assets/i18n', '.po');
}

const translations = navigator.language || 'en_US';

@NgModule({
    declarations: [
        AppComponent,
        MessageBoxComponent,
        NavigationBarComponent,
        EditableLabelComponent,
        DevicesComponent,
        EventsComponent,
        EventEditComponent,
        ScenariosComponent
    ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: (createTranslateLoader),
                deps: [HttpClient]
            }
        }),
        HttpClientModule,
        FlexLayoutModule,
        FormsModule,
        MatInputModule, MatButtonModule, MatCardModule, MatCheckboxModule,
        MatIconModule, MatMenuModule, MatSelectModule, MatToolbarModule,
        StoreModule.forRoot({}, {
            runtimeChecks: {
                strictStateImmutability: true,
                strictActionImmutability: true,
                strictStateSerializability: true,
                strictActionSerializability: true,
            },
        }),
        StoreModule.forFeature(EVENT_STATE, eventReducer),
        EffectsModule.forRoot([EventEffects])
    ],
    providers: [
        {provide: LOCALE_ID, useValue: translations},
        {provide: TranslateParser, useFactory: (createTranslateParser)},
        {provide: MissingTranslationHandler, useClass: InterpolatedMissingTranslationHandler},
        TranslateService,
        DeviceService,
        EventService,
        ScenarioService,
        MessageService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
