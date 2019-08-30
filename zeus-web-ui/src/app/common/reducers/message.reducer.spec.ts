import {messageReducer} from './message.reducer';
import {initialState} from '../model/message-state';
import * as MessageActions from '../actions/message.actions';
import {Level, Message} from '../model/message';

describe('MessageReducer', () => {
    it('should return the default state when action is undefined', () => {
        // given nothing special

        // when
        const state = messageReducer(undefined, {type: undefined});

        // then
        expect(state).toBe(initialState);
    });

    it('should update message in state when display action is triggered', () => {
        // given
        const message = <Message>{level: Level.INFO, text: 'Test Message!'};

        // when
        const state = messageReducer(initialState, MessageActions.display({message: message}));

        // then
        expect(state.message).toBe(message);
    });
});
