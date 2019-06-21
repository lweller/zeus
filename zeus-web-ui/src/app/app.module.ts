import {AppRoutingModule} from './app-routing.module';
import {LOCALE_ID, NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {DevicesComponent} from './devices/comonents/devices.component';
import {ScenariosComponent} from './scenarios/components/scenarios.component';
import {DeviceService} from './devices/services/device.service';
import {ScenarioService} from './scenarios/services/scenario.service';
import {MetaReducer, StoreModule} from "@ngrx/store";
import {ZeusCommonModule} from "./common/common.module";
import {HttpClient, HttpClientModule} from "@angular/common/http";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {EffectsModule} from "@ngrx/effects";
import {messageStateReducer} from "./common/reducers/message.metareducer";
import {
    MissingTranslationHandler,
    MissingTranslationHandlerParams,
    TranslateDefaultParser,
    TranslateLoader,
    TranslateModule,
    TranslateParser,
    TranslateService
} from "@ngx-translate/core";
import {registerLocaleData} from "@angular/common";
import localeEn from "@angular/common/locales/en";
import localeDe from "@angular/common/locales/de";
import localeFr from "@angular/common/locales/fr";
import {TranslatePoHttpLoader} from "./common/utils/translate-po-http-loader";
import {EventsModule} from "./events/events.module";

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
export const metaReducers: MetaReducer<any>[] = [messageStateReducer];

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
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: (createTranslateLoader),
                deps: [HttpClient]
            }
        }),
        StoreModule.forRoot({}, {
            metaReducers: metaReducers,
            runtimeChecks: {
                strictStateImmutability: true,
                strictActionImmutability: true,
                strictStateSerializability: true,
                strictActionSerializability: true,
            },
        }),
        EffectsModule.forRoot([]),
        AppRoutingModule,
        EventsModule
    ],
    providers: [
        {provide: LOCALE_ID, useValue: translations},
        {provide: TranslateParser, useFactory: (createTranslateParser)},
        {provide: MissingTranslationHandler, useClass: InterpolatedMissingTranslationHandler},
        TranslateService,
        DeviceService,
        ScenarioService,
    ],
    bootstrap: [AppComponent]
})

export class AppModule {
}
