import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';

@Injectable()
export class TranslateMockService {
    // noinspection JSUnusedGlobalSymbols,JSUnusedLocalSymbols
    get(key: any, interpolateParams?: Object): Observable<any> {
        return of(key);
    }
}
