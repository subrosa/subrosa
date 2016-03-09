import {Injectable} from "angular2/core";
import {Client} from "./client";
import {Login} from "./login";
import {Headers} from "angular2/http";
import {Http} from "angular2/http";
import {User} from "./user";
import {Subject} from "rxjs/Subject";
import {BehaviorSubject} from "rxjs/Rx";
import {OnInit} from "angular2/core";
import {Observable} from "rxjs/Observable";

var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(e){var t="";var n,r,i,s,o,u,a;var f=0;e=Base64._utf8_encode(e);while(f<e.length){n=e.charCodeAt(f++);r=e.charCodeAt(f++);i=e.charCodeAt(f++);s=n>>2;o=(n&3)<<4|r>>4;u=(r&15)<<2|i>>6;a=i&63;if(isNaN(r)){u=a=64}else if(isNaN(i)){a=64}t=t+this._keyStr.charAt(s)+this._keyStr.charAt(o)+this._keyStr.charAt(u)+this._keyStr.charAt(a)}return t},decode:function(e){var t="";var n,r,i;var s,o,u,a;var f=0;e=e.replace(/[^A-Za-z0-9\+\/\=]/g,"");while(f<e.length){s=this._keyStr.indexOf(e.charAt(f++));o=this._keyStr.indexOf(e.charAt(f++));u=this._keyStr.indexOf(e.charAt(f++));a=this._keyStr.indexOf(e.charAt(f++));n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;t=t+String.fromCharCode(n);if(u!=64){t=t+String.fromCharCode(r)}if(a!=64){t=t+String.fromCharCode(i)}}t=Base64._utf8_decode(t);return t},_utf8_encode:function(e){e=e.replace(/\r\n/g,"\n");var t="";for(var n=0;n<e.length;n++){var r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r)}else if(r>127&&r<2048){t+=String.fromCharCode(r>>6|192);t+=String.fromCharCode(r&63|128)}else{t+=String.fromCharCode(r>>12|224);t+=String.fromCharCode(r>>6&63|128);t+=String.fromCharCode(r&63|128)}}return t},_utf8_decode:function(e){var t="";var n=0;var r=c1=c2=0;while(n<e.length){r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r);n++}else if(r>191&&r<224){c2=e.charCodeAt(n+1);t+=String.fromCharCode((r&31)<<6|c2&63);n+=2}else{c2=e.charCodeAt(n+1);c3=e.charCodeAt(n+2);t+=String.fromCharCode((r&15)<<12|(c2&63)<<6|c3&63);n+=3}}return t}}

@Injectable()
export class AuthService {
    user: BehaviorSubject<User> = new BehaviorSubject<User>(null);
    jwt: BehaviorSubject<string> = new BehaviorSubject<string>(null);
    client: BehaviorSubject<Client> = new BehaviorSubject<Client>(null);
    clientJwt: BehaviorSubject<string> = new BehaviorSubject<string>(null);
    private clientId: string;
    private clientSecret: string;

    constructor(private _http: Http/*, private _config: SrConfig*/) {
        this.clientId = localStorage.getItem('clientId');
        this.clientSecret = localStorage.getItem('clientSecret');
        if (this.clientId && this.clientSecret) {
            this.clientCredentialsGrant();
        }

        this.jwt.subscribe(token => {
            if (token == null) {
                localStorage.removeItem('jwt');
            } else {
                localStorage.setItem('jwt', token);
                this.userDetails(token);
            }
        });
        this.clientJwt.subscribe(token => {
            if (token == null) {
                localStorage.removeItem('clientJwt');
            } else {
                localStorage.setItem('clientJwt', token);
                this.clientDetails(token);
            }
        });

        this.jwt.next(localStorage.getItem('jwt'));
        this.clientJwt.next(localStorage.getItem('clientJwt'));
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
        this._http.post("http://sr:8080/oauth/token", creds, {headers: headers})
            .subscribe(
                response => this.clientJwt.next(response.json().access_token),
                error => console.error(error),
                () => console.log('Login call complete')
            );
    }

    clientDetails(token: string) {
        this._http.get("http://sr:8080/client", {headers: this._authorizationHeader(token)})
            .subscribe(
                response => this.client.next(response.json()),
                error => console.error(error),
                () => console.log('Client details call complete')
            );
    }

    passwordGrant(login: Login) {
        var headers = this._clientAuthHeaders();
        headers.append('Content-Type', 'application/x-www-form-urlencoded');
        var creds = "username=" + login.email + "&password=" + login.password + "&grant_type=password";
        this._http.post("http://sr:8080/oauth/token", creds, {headers: headers})
            .subscribe(
                response => this.jwt.next(response.json().access_token),
                error => console.error(error),
                () => console.log('Login call complete')
            );
    }

    userDetails(token: string) {
        this._http.get("http://sr:8080/user", {headers: this._authorizationHeader(token)})
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
        headers.set('Authorization', 'Bearer ' + token);
        return headers;
    }

    private _clientAuthHeaders(): Headers {
        var headers = new Headers();
        var authdata = Base64.encode(this.clientId + ':' + this.clientSecret);
        headers.append('Authorization', 'Basic ' + authdata);
        return headers;
    }

}