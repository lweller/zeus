import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {DevicesComponent} from './component/device/devices.component';
import {EventsComponent} from './component/scenario/events.component';

const routes: Routes = [
  {path: '', redirectTo: '/devices', pathMatch: 'full'},
  {path: 'devices', component: DevicesComponent},
  {path: 'events', component: EventsComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
