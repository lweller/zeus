import {Component, OnInit} from '@angular/core';
import {Scenario} from "../model/scenario";
import {ActivatedRoute, Router} from "@angular/router";
import * as ScenarioActions from "../actions/scenario-ui.actions";
import {select, Store} from "@ngrx/store";
import {editedScenario} from "../model/scenario-state";
import * as lodash from 'lodash';

@Component({
    selector: 'app-scenario-edit',
    templateUrl: './scenario-edit.component.html',
    styleUrls: ['./scenario-edit.component.css']
})
export class ScenarioEditComponent implements OnInit {

    scenario: Scenario;

    constructor(private store: Store<any>,
                private router: Router,
                private route: ActivatedRoute) {
        store.pipe(select(editedScenario)).subscribe(scenario => scenario == undefined ? this.close() : this.scenario = lodash.cloneDeep(scenario));
    }

    ngOnInit() {
    }

    save(scenario: Scenario): void {
        this.store.dispatch(ScenarioActions.modified({scenario: scenario}));
    }

    close() {
        this.router.navigate(['..'], {relativeTo: this.route}).then()
    }
}
