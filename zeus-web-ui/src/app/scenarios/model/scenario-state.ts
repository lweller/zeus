import {createFeatureSelector, createSelector} from "@ngrx/store";
import {Scenario} from "./scenario";

export interface ScenarioState {
    scenarios: Scenario[];
    editedScenario: Scenario;
}

export const initialScenarioState: ScenarioState = {
    scenarios: null,
    editedScenario: null
};

export const SCENARIO_STATE_ID = 'ch.wellernet.zeus.scenarios';

const eventState = createFeatureSelector<ScenarioState>(SCENARIO_STATE_ID);

export const scenarios = createSelector(eventState, (state: ScenarioState) => state.scenarios);
export const editedScenario = createSelector(eventState, (state: ScenarioState) => state.editedScenario);