import {Injectable} from "@angular/core";
import {Observable, of} from "rxjs";

@Injectable()
export class TranslateMockService {
    public static get(key: any): Observable<any> {
        return of(key);
    }
}