import {Component, OnInit} from '@angular/core';
import {Scenario} from '../model/scenario';
import {select, Store} from "@ngrx/store";
import * as ScenarioUiActions from "../actions/scenario-ui.actions";
import {scenarios} from "../model/scenario-state";
import {cloneDeep} from 'lodash';
import * as ScenarioActions from "../../scenarios/actions/scenario-ui.actions";

@Component({
    selector: 'app-scenarios',
    templateUrl: './scenarios.component.html',
    styleUrls: ['./scenarios.component.css']
})
export class ScenariosComponent implements OnInit {

    scenarios: Scenario[];

    constructor(private store: Store<any>) {
        store.pipe(select(scenarios)).subscribe(scenarios => this.scenarios = cloneDeep(scenarios));
    }

    ngOnInit() {
        this.store.dispatch(ScenarioUiActions.init())
    }

    edit(scenario: Scenario) {
        this.store.dispatch(ScenarioActions.edit({scenario: cloneDeep(scenario)}))
    }

    save(scenario: Scenario): void {
        this.store.dispatch(ScenarioActions.modified({scenario: cloneDeep(scenario)}));
    }

    toggleEnabling(scenario: Scenario): void {
        this.store.dispatch(ScenarioActions.toggleEnabling({scenario: cloneDeep(scenario)}));
    }
}
