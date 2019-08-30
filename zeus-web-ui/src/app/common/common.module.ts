import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MessageBoxComponent} from './component/message-box/message-box.component';
import {NavigationBarComponent} from './component/navigation-bar/navigation-bar.component';
import {EditableLabelComponent} from './component/editable-label/editable-label.component';
import {TranslateModule} from '@ngx-translate/core';
import {RouterModule} from '@angular/router';
import {StoreModule} from '@ngrx/store';
import {MESSAGE_STATE} from './model/message-state';
import {messageReducer} from './reducers/message.reducers';

@NgModule({
    imports: [
        CommonModule,
        RouterModule,
        StoreModule.forFeature(MESSAGE_STATE, messageReducer)
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
    ]
})
export class ZeusCommonModule {
}
