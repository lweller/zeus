import {InjectionToken} from "@angular/core";

export const CONFIG = new InjectionToken<DevicesModuleConfig>('devices-config');

export abstract class DevicesModuleConfig {
    root: string
}