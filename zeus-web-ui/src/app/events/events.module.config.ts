import {InjectionToken} from "@angular/core";

export const CONFIG = new InjectionToken<EventsModuleConfig>('events-config');

export abstract class EventsModuleConfig {
    root: string
}