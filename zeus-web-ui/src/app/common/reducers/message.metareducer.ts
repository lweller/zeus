import {ActionReducer} from '@ngrx/store';
import {ACTION_TYPE_PREFIX} from '../utils/message.util';

export function messageStateReducer(reducer: ActionReducer<any>): ActionReducer<any> {
    return function (state, action) {
        if (!action.type.startsWith(ACTION_TYPE_PREFIX)) {
            console.log(action.type);
            return reducer(state, action);
        }
        let actionInfo;
        try {
            actionInfo = JSON.parse(action.type.substring(ACTION_TYPE_PREFIX.length));
        } catch (e) {
            console.error(e);
        }
        console.log(actionInfo && actionInfo.source && actionInfo.id ? `[${actionInfo.source}] ${actionInfo.id}` : action.type);
        if (!actionInfo || !actionInfo.level || !actionInfo.message) {
            return reducer(state, action);
        }
        return reducer({
            ...state,
            messageState: {
                message: {
                    level: actionInfo.level,
                    text: actionInfo.message,
                    params: action
                }
            }
        }, action);
    };
}
