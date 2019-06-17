import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DevicesComponent} from './component/device/devices.component';
import {EventsComponent} from './component/event/events.component';
import {EventEditComponent} from "./component/event/event.edit.component";
import {ScenariosComponent} from './component/scenario/scenarios.component';

const routes: Routes = [
    {path: '', redirectTo: '/devices', pathMatch: 'full'},
    {path: 'devices', component: DevicesComponent},
    {path: 'events', component: EventsComponent},
    {path: 'events/:id', component: EventEditComponent},
    {path: 'scenarios', component: ScenariosComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
