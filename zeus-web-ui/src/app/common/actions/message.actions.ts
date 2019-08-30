import {createAction, props} from '@ngrx/store';
import {Message} from '../model/message';

export const display = createAction('[Message] Display Message', props<{ message: Message }>());
