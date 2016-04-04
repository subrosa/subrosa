import {Injectable} from "angular2/core";
import {Observable} from "rxjs/Observable";
import {Account} from "./account";
import {Http} from "angular2/http";
import {AuthService} from "../user/auth.service";
import {Observer} from "rxjs/Observer";
import {SettingsService} from "../settings/settings.service";

@Injectable()
export class AccountService {

    accounts$: Observable<Array<Account>>;
    private _accountsObserver: Observer<Array<Account>>;

    constructor(private _http: Http,
                private _auth: AuthService,
                private _settings: SettingsService) {
        this.accounts$ = new Observable(obs => this._accountsObserver = obs).share();
    }

    loadAccounts() {
        console.log('loadAccounts');
        this._auth.clientTokenHeaders().withLatestFrom(this._settings.apiRoot()).subscribe(inputs => {
            let headers = inputs[0];
            let apiRoot = inputs[1];
            this._http.get(`${apiRoot}/subrosa/v1/account`, {headers: headers})
                .map(response => response.json())
                .subscribe(data => {
                    console.log(data);
                    this._accountsObserver.next(data.results);
                }, error => console.error('Failed to load accounts.', error));
        });
    }
}