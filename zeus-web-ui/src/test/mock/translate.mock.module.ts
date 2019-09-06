import {NgModule} from '@angular/core';
import {TranslatePipeMock} from './translate.pipe.mock';

@NgModule({
    declarations: [TranslatePipeMock],
    exports: [TranslatePipeMock]
})
export class TranslateMockModule {
}
