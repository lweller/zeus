import {InjectionToken} from "@angular/core";

export const CONFIG = new InjectionToken<ScenariosModuleConfig>('scenarios-config');


export abstract class ScenariosModuleConfig {
    root: string;
}