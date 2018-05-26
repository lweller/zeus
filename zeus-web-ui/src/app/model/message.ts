export const STATE_NEW = 'new';
export const STATE_DONE = 'done';

export const LEVEL_INFO = 'info';
export const LEVEL_WARNING = 'warning';
export const LEVEL_ERROR = 'error';

export class Message {
  message = '';
  state = STATE_DONE;
  level: string;
}
