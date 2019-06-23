import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {EventsComponent} from './components/events.component';
import {EventEditComponent} from "./components/event-edit.component";

const routes: Routes = [
    {path: '', component: EventsComponent},
    {path: 'edit', component: EventEditComponent},
];

@NgModule({
    imports: [RouterModule.forChild(routes)]
})
export class EventsRoutingModule {
}
