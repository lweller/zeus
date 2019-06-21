import {inject, TestBed} from '@angular/core/testing';

import {DeviceService} from "./device.service";
import {TranslateService} from "@ngx-translate/core";
import {TranslateMockService} from "../../common/service/translate.service.mock";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('DeviceService', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [DeviceService, {provide: TranslateService, useClass: TranslateMockService}]
        });
    });

    it('should be created', inject([DeviceService], (service: DeviceService) => {
        expect(service).toBeTruthy();
    }));
});
