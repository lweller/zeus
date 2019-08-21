import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

export const DEVICES_ROOT = 'devices';
export const EVENTS_ROOT = 'events';
export const SCENARIOS_ROOT = 'scenarios';

const routes: Routes = [
    {path: '', redirectTo: '/devices', pathMatch: 'full'},
    {path: DEVICES_ROOT, loadChildren: './devices/devices.module#DevicesModule'},
    {path: EVENTS_ROOT, loadChildren: './events/events.module#EventsModule'},
    {path: SCENARIOS_ROOT, loadChildren: './scenarios/scenarios.module#ScenariosModule'}
];

@NgModule({
    imports: [RouterModule.forRoot(routes, {relativeLinkResolution: 'corrected'})],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
