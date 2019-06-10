import {AppRoutingModule} from './app-routing.module';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgModule, LOCALE_ID} from '@angular/core';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {
  TranslateModule, TranslateLoader, TranslateService, TranslateDefaultParser,
  TranslateParser, MissingTranslationHandler, MissingTranslationHandlerParams
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
    DevicesComponent,
    EventsComponent,
    ScenariosComponent,
    MessageBoxComponent,
    NavigationBarComponent,
    EditableLabelComponent
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
    HttpClientModule
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
export class AppModule {}
