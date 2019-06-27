import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DevicesComponent} from './devices/comonents/devices.component';

export const EVENTS_ROOT = 'events';
export const SCENARIOS_ROOT = 'scenarios';

const routes: Routes = [
    {path: '', redirectTo: '/devices', pathMatch: 'full'},
    {path: 'devices', component: DevicesComponent},
    {path: EVENTS_ROOT, loadChildren: './events/events.module#EventsModule'},
    {path: SCENARIOS_ROOT, loadChildren: './scenarios/scenarios.module#ScenariosModule'}
];

@NgModule({
    imports: [RouterModule.forRoot(routes, {relativeLinkResolution: 'corrected'})],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
