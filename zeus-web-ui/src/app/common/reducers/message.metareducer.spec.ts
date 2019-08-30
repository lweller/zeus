import {messageStateReducer} from './message.metareducer';
import {createAction} from '@ngrx/store';
import {Level} from '../model/message';
import {ACTION_TYPE_PREFIX, createActionType} from '../utils/message.util';

describe('MessageMetaReducer', () => {
    it('should call reducer and log action type', () => {
        // given
        const state = {something: true};
        const reducer = jasmine.createSpy();
        const action = createAction('test action');
        spyOn(console, 'log');

        // when
        messageStateReducer(reducer)(state, action);

        // then
        expect(reducer).toHaveBeenCalledWith(state, action);
        expect(console.log).toHaveBeenCalledWith(action.type);
    });

    it('should not add new message to state if type of action is prefixed and contains no message level in JSON', () => {
        // given
        const reducer = jasmine.createSpy();
        const action = createAction(createActionType('source', 'test', 'Test Message!', undefined));
        spyOn(console, 'log');

        // when
        messageStateReducer(reducer)({}, action);

        // then
        expect(reducer).toHaveBeenCalledWith({}, action);
        expect(console.log).toHaveBeenCalledWith('[source] test');
    });

    it('should not add new message to state if type of action is prefixed and contains no message text in JSON', () => {
        // given
        const reducer = jasmine.createSpy();
        const action = createAction(createActionType('source', 'test', undefined, Level.INFO));
        spyOn(console, 'log');

        // when
        messageStateReducer(reducer)({}, action);

        // then
        expect(reducer).toHaveBeenCalledWith({}, action);
        expect(console.log).toHaveBeenCalledWith('[source] test');
    });

    it('should add new message to state if type of action is prefixed and contains all message properties in JSON', () => {
        // given
        const reducer = jasmine.createSpy();
        const action = createAction(createActionType('source', 'test', 'Test Message!', Level.INFO));
        spyOn(console, 'log');

        // when
        messageStateReducer(reducer)({}, action);

        // then
        expect(reducer).toHaveBeenCalledWith({
            messageState: {
                message: {
                    level: Level.INFO,
                    text: 'Test Message!',
                    params: action
                }
            }
        }, action);
        expect(console.log).toHaveBeenCalledWith('[source] test');
    });

    it('should not add new message to state if type of action is prefixed and contains an empty JSON', () => {
        // given
        const reducer = jasmine.createSpy();
        const action = createAction(`${ACTION_TYPE_PREFIX}{}`);
        spyOn(console, 'log');

        // when
        messageStateReducer(reducer)({}, action);

        // then
        expect(reducer).toHaveBeenCalledWith({}, action);
        expect(console.log).toHaveBeenCalledWith(`${ACTION_TYPE_PREFIX}{}`);
    });

    it('should not add new message to state if type of action is prefixed and contains no JSON', () => {
        // given
        const reducer = jasmine.createSpy();
        const action = createAction(ACTION_TYPE_PREFIX);
        spyOn(console, 'log');

        // when
        messageStateReducer(reducer)({}, action);

        // then
        expect(reducer).toHaveBeenCalledWith({}, action);
        expect(console.log).toHaveBeenCalledWith(ACTION_TYPE_PREFIX);
    });
});
