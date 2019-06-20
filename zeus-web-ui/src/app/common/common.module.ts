import {LOCALE_ID, NgModule} from '@angular/core';
import {CommonModule, registerLocaleData} from '@angular/common';
import {MessageBoxComponent} from "./component/message-box/message-box.component";
import {NavigationBarComponent} from "./component/navigation-bar/navigation-bar.component";
import {EditableLabelComponent} from "./component/editable-label/editable-label.component";
import {
    MissingTranslationHandler,
    MissingTranslationHandlerParams,
    TranslateDefaultParser,
    TranslateLoader,
    TranslateModule,
    TranslateParser,
    TranslateService
} from "@ngx-translate/core";
import {MessageService} from "./service/message.service";
import {HttpClient} from "@angular/common/http";
import localeEn from "@angular/common/locales/en";
import localeDe from "@angular/common/locales/de";
import localeFr from "@angular/common/locales/fr";
import {TranslatePoHttpLoader} from "./utils/translate-po-http-loader";
import {RouterModule} from "@angular/router";

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
    imports: [
        CommonModule,
        RouterModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: (createTranslateLoader),
                deps: [HttpClient]
            }
        }),
    ],
    exports: [
        CommonModule,
        TranslateModule,
        NavigationBarComponent,
        MessageBoxComponent,
        EditableLabelComponent
    ],
    declarations: [
        NavigationBarComponent,
        MessageBoxComponent,
        EditableLabelComponent,
    ],
    providers: [
        {provide: LOCALE_ID, useValue: translations},
        {provide: TranslateParser, useFactory: (createTranslateParser)},
        {provide: MissingTranslationHandler, useClass: InterpolatedMissingTranslationHandler},
        TranslateService,
        MessageService
    ]
})
export class ZeusCommonModule {
}