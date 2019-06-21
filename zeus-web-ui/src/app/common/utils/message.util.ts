export interface ActionTye {
    type: string,
    message: string,
    level: string
}

export const ACTION_TYPE_PREFIX = '@zeus';

export function createActionType(source: string, id: string, message?: string, level?: string): string {
    return ACTION_TYPE_PREFIX + JSON.stringify({
        source: source,
        id: id,
        message: message,
        level: level

    })
}