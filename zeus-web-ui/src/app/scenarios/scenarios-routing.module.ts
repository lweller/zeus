import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ScenariosComponent} from "./components/scenarios.component";
import {ScenarioEditComponent} from "./components/scenario-edit.component";

const routes: Routes = [
    {path: '', component: ScenariosComponent},
    {path: 'edit', component: ScenarioEditComponent}
];

@NgModule({
    imports: [RouterModule.forChild(routes)]
})
export class ScenariosRoutingModule {
}
