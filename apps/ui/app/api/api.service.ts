import {Injectable} from "angular2/core";
import {Headers} from "angular2/http";
import {Http} from "angular2/http";
import {RequestOptionsArgs} from "angular2/http";

@Injectable()
export class AuthService {

    baseUrl: string = "http://sr:8080/";

    constructor(private _http: Http/*, private _config: SrConfig*/) {
    }

    get(path: string, options?: RequestOptionsArgs) {
        return this._http.get(this.baseUrl + path, options)
    }

    post(path: string, body: string, options?: RequestOptionsArgs) {
        return this._http.post(this.baseUrl + path, body, options)
    }

}