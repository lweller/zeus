import {createFeatureSelector, createSelector} from "@ngrx/store";
import {Message} from "./message";

export interface MessageState {
    message: Message;
}

export const initialState: MessageState = {
    message: null,
};

export const MESSAGE_STATE = 'messageState';

const messageState = createFeatureSelector<MessageState>(MESSAGE_STATE);

export const message = createSelector(messageState, (state: MessageState) => state.message);