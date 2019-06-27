import {Action, createReducer, on} from "@ngrx/store";
import {initialScenarioState, ScenarioState} from "../model/scenario-state";
import * as ScenarioApiActions from "../../scenarios/actions/scenario-api.actions";
import * as ScenarioUiActions from "../../scenarios/actions/scenario-ui.actions";

const reducer = createReducer(initialScenarioState,
    on(
        ScenarioApiActions.loadedAllSuccessfully,
        (state, {scenarios}) => ({...state, scenarios: scenarios})),

    on(
        ScenarioUiActions.edit,
        (state, {scenario}) => ({
            ...state,
            editedScenario: scenario
        })),

    on(
        ScenarioApiActions.savedSuccessfully,
        (state) => ({
            ...state,
            editedScenario: null
        })),

    on(
        ScenarioUiActions.modified,
        ScenarioApiActions.refresh,
        (state, {scenario}) => ({
            ...state,
            editedScenario: state.editedScenario && state.editedScenario.id === scenario.id ? scenario : state.editedScenario,
            scenarios: state.scenarios.map(otherScenario => otherScenario.id === scenario.id ? scenario : otherScenario)
        }))
);

export function scenarioReducer(state: ScenarioState | undefined, action: Action) {
    return reducer(state, action);
}