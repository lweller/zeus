import {Action, createReducer, on} from "@ngrx/store";
import * as MessageActions from "../actions/message.actions";
import {initialState, MessageState} from "../model/message-state";

const reducer = createReducer(
    initialState,
    on(MessageActions.display, (state, {message}) => ({...state, message: message}))
);

export function messageReducer(state: MessageState | undefined, action: Action) {
    return reducer(state, action);
}