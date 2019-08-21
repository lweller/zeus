import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DevicesComponent} from './components/devices.component';
import {DeviceEditComponent} from "./components/device-edit.component";

const routes: Routes = [
    {path: '', component: DevicesComponent},
    {path: 'edit', component: DeviceEditComponent},
];

@NgModule({
    imports: [RouterModule.forChild(routes)]
})
export class DevicesRoutingModule {
}
