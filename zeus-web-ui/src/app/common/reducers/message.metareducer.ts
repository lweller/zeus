import {ActionReducer} from '@ngrx/store';
import {ACTION_TYPE_PREFIX} from '../utils/message.util';

export function messageStateReducer(reducer: ActionReducer<any>): ActionReducer<any> {
    return function (state, action) {
        if (action.type.startsWith(ACTION_TYPE_PREFIX)) {
            const actionInfo = JSON.parse(action.type.substring(ACTION_TYPE_PREFIX.length));
            console.log(`[${actionInfo.source}] ${actionInfo.id}`);
            if (!actionInfo.level || !actionInfo.message) {
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
        }
        console.log(action.type);
        return reducer(state, action);
    };
}
