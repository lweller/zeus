import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DevicesComponent} from './devices/comonents/devices.component';
import {ScenariosComponent} from './scenarios/components/scenarios.component';

const routes: Routes = [
    {path: '', redirectTo: '/devices', pathMatch: 'full'},
    {path: 'devices', component: DevicesComponent},
    {path: 'events', loadChildren: './events/events.module#EventsModule'},
    {path: 'scenarios', component: ScenariosComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
