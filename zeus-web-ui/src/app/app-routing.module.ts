import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DevicesComponent} from './devices/comonents/devices.component';
import {EventsComponent} from './events/components/events.component';
import {EventEditComponent} from "./events/components/event-edit.component";
import {ScenariosComponent} from './scenarios/components/scenarios.component';

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
