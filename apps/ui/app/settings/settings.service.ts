import {Injectable} from "angular2/core";
import {BehaviorSubject} from "rxjs/Rx";
import {Observable} from "rxjs/Observable";

@Injectable()
export class SettingsService {
    private _apiRoot$: BehaviorSubject<string>;

    constructor() {
        this._apiRoot$ = new BehaviorSubject<string>("http://sr:8080");

        var apiRoot = localStorage.getItem('apiRoot');
        if (apiRoot != null) {
            this._apiRoot$.next(apiRoot);
        }
    }
    
    apiRoot(): Observable<string> {
        return this._apiRoot$.asObservable();
    }
    
    setApiRoot(apiRoot: string) {
        if (apiRoot == null) {
            localStorage.removeItem('apiRoot');
        } else {
            localStorage.setItem('apiRoot', apiRoot);
        }
        this._apiRoot$.next(apiRoot);
    }
    
}