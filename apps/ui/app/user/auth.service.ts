import {Injectable, Inject} from "angular2/core";
import {Client} from "./client";
import {Login} from "./login";
import {Headers} from "angular2/http";
import {Http} from "angular2/http";
import {User} from "./user";
import {BehaviorSubject} from "rxjs/Rx";
import {Observable} from "rxjs/Observable";
import {Base64} from "./base64";
import {SettingsService} from "../settings/settings.service";

@Injectable()
export class AuthService {
    user: BehaviorSubject<User> = new BehaviorSubject<User>(null);
    jwt: BehaviorSubject<string>;
    client: BehaviorSubject<Client> = new BehaviorSubject<Client>(null);
    clientJwt: BehaviorSubject<string>;
    private clientId: string;
    private clientSecret: string;
    private apiRoot: string;

    constructor(private _http: Http,
                @Inject(SettingsService) private _settings: SettingsService) {
        this.jwt = new BehaviorSubject(localStorage.getItem('jwt'));
        this.jwt.withLatestFrom(this._settings.apiRoot()).subscribe(inputs => {
            let token = inputs[0];
            if (token == null) {
                localStorage.removeItem('jwt');
            } else {
                localStorage.setItem('jwt', token);
                this.userDetails(token);
            }
        });
        this.clientJwt = new BehaviorSubject(localStorage.getItem('clientJwt'));
        this.clientJwt.withLatestFrom(this._settings.apiRoot()).subscribe(inputs => {
            let token = inputs[0];
            if (token == null) {
                localStorage.removeItem('clientJwt');
            } else {
                localStorage.setItem('clientJwt', token);
                this.clientDetails(token);
            }
        });

        this.clientId = localStorage.getItem('clientId');
        this.clientSecret = localStorage.getItem('clientSecret');
        this._settings.apiRoot().subscribe(apiRoot => {
            this.apiRoot = apiRoot;
            if (this.clientId && this.clientSecret) {
                this.clientCredentialsGrant();
            }
        });
    }

    clientTokenHeaders(): Observable<Headers> {
        return this.clientJwt
            .filter(Boolean)
            .map(token => this._authorizationHeader(token));
    }

    userTokenHeaders(): Observable<Headers> {
        return this.jwt
            .filter(Boolean)
            .map(token => this._authorizationHeader(token));
    }

    updateClientCredentials(client: Client) {
        this.clientId = client.clientId;
        this.clientSecret = client.secret;
        localStorage.setItem('clientId', this.clientId);
        localStorage.setItem('clientSecret', this.clientSecret);
    }

    clientCredentialsGrant() {
        var headers = this._clientAuthHeaders();
        headers.append('Content-Type', 'application/x-www-form-urlencoded');
        var creds = "grant_type=client_credentials";
        this._http.post(this._apiUrl("oauth/token"), creds, {headers: headers})
            .subscribe(
                response => this.clientJwt.next(response.json().access_token),
                error => console.error(error),
                () => console.log('Login call complete')
            );
    }

    clientDetails(token: string) {
        this._http.get(this._apiUrl("client"), {headers: this._authorizationHeader(token)})
            .subscribe(
                response => this.client.next(response.json()),
                error => console.error(error),
                () => console.log('Client details call complete')
            );
    }

    passwordGrant(login: Login) {
        var headers = this._clientAuthHeaders();
        headers.append('Content-Type', 'application/x-www-form-urlencoded');
        var creds = `username=${login.email}&password=${login.password}`;
        this._http.post(this._apiUrl("oauth/token"), creds, {headers: headers})
            .subscribe(
                response => this.jwt.next(response.json().access_token),
                error => console.error(error),
                () => console.log('Login call complete')
            );
    }

    userDetails(token: string) {
        this._http.get(this._apiUrl("user"), {headers: this._authorizationHeader(token)})
            .subscribe(
                response => this.user.next(response.json()),
                error => console.error(error),
                () => console.log('User details call complete')
            );
    }

    resetClient() {
        this.clientId = null;
        this.clientSecret = null;
        localStorage.removeItem('clientId');
        localStorage.removeItem('clientSecret');
        this.clientJwt.next(null);
        this.client.next(null);
    }

    resetUser() {
        this.jwt.next(null);
        this.user.next(null);
    }

    private _authorizationHeader(token: string): Headers {
        var headers = new Headers();
        headers.set('Authorization', `Bearer ${token}`);
        return headers;
    }

    private _clientAuthHeaders(): Headers {
        var headers = new Headers();
        var authdata = Base64.encode(`${this.clientId}:${this.clientSecret}`);
        headers.append('Authorization', `Basic ${authdata}`);
        return headers;
    }

    private _apiUrl(path) {
        var url = this.apiRoot + "/" + path;
        console.log(url);
        return url
    }

}