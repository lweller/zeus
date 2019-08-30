export enum Level {
    INFO = 'info',
    WARNING = 'warning',
    ERROR = 'error'
}

export enum State {
    NEW,
    ACKNOWLEDGED
}

export interface Message {
    level: Level;
    text: string;
    params: any;
}
