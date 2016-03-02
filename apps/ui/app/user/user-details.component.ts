import {Component} from 'angular2/core';
import {CORE_DIRECTIVES} from "angular2/common";
import {Http, Headers} from 'angular2/http';
import {User} from "app/user/user";
import {Login} from "app/user/login";
import {AuthService} from "./auth.service";

var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(e){var t="";var n,r,i,s,o,u,a;var f=0;e=Base64._utf8_encode(e);while(f<e.length){n=e.charCodeAt(f++);r=e.charCodeAt(f++);i=e.charCodeAt(f++);s=n>>2;o=(n&3)<<4|r>>4;u=(r&15)<<2|i>>6;a=i&63;if(isNaN(r)){u=a=64}else if(isNaN(i)){a=64}t=t+this._keyStr.charAt(s)+this._keyStr.charAt(o)+this._keyStr.charAt(u)+this._keyStr.charAt(a)}return t},decode:function(e){var t="";var n,r,i;var s,o,u,a;var f=0;e=e.replace(/[^A-Za-z0-9\+\/\=]/g,"");while(f<e.length){s=this._keyStr.indexOf(e.charAt(f++));o=this._keyStr.indexOf(e.charAt(f++));u=this._keyStr.indexOf(e.charAt(f++));a=this._keyStr.indexOf(e.charAt(f++));n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;t=t+String.fromCharCode(n);if(u!=64){t=t+String.fromCharCode(r)}if(a!=64){t=t+String.fromCharCode(i)}}t=Base64._utf8_decode(t);return t},_utf8_encode:function(e){e=e.replace(/\r\n/g,"\n");var t="";for(var n=0;n<e.length;n++){var r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r)}else if(r>127&&r<2048){t+=String.fromCharCode(r>>6|192);t+=String.fromCharCode(r&63|128)}else{t+=String.fromCharCode(r>>12|224);t+=String.fromCharCode(r>>6&63|128);t+=String.fromCharCode(r&63|128)}}return t},_utf8_decode:function(e){var t="";var n=0;var r=c1=c2=0;while(n<e.length){r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r);n++}else if(r>191&&r<224){c2=e.charCodeAt(n+1);t+=String.fromCharCode((r&31)<<6|c2&63);n+=2}else{c2=e.charCodeAt(n+1);c3=e.charCodeAt(n+2);t+=String.fromCharCode((r&15)<<12|(c2&63)<<6|c3&63);n+=3}}return t}}

@Component({
    selector: 'user-details',
    template: `
<div>
<div class="home jumbotron centered">
<div *ngIf="user">
    <h2>You are currently logged in as {{ user.username }} (id {{ user.id }}).</h2>
    <p><a class="btn btn-primary btn-lg" role="button" (click)="logout()">Logout</a></p>
</div>
<div *ngIf="!user">
    <h2>You are not logged in.</h2>
    <div>
        <label>email: </label>
        <input [(ngModel)]="login.email" placeholder="email"/>
    </div>
    <div>
        <label>password: </label>
        <input [(ngModel)]="login.password" type="password"/>
    </div>
    <div>
        <a class="btn btn-primary btn-lg" role="button" (click)="login()">Login</a>
    </div>
</div>
</div>
</div>
    `,
    styles: [`
    `],
    directives: [CORE_DIRECTIVES],
    providers: [AuthService],
    inputs: ['user'],
})
export class UserDetailsComponent {
    user: User;
    login: Login;
    accessToken: string;

    constructor(
        private _auth: AuthService,
        private _http: Http) {
    }

    logout() {
        this.user = null;
        this.accessToken = null;
    }

    login() {
        var headers = new Headers();
        headers.append('Content-Type', 'application/x-www-form-urlencoded');
        var authdata = Base64.encode('subrosa:subrosa');
        headers.append('Authorization', 'Basic ' + authdata);
        var creds = "username=" + this.login.email + "&password=" + this.login.password + "&grant_type=password";
        this._http.post("http://sr:8080/oauth/token", creds, {headers: headers})
            .subscribe(
                response => {
                    this.accessToken = response.json().access_token;
                    this.getUser();
                },
                error => console.error(error),
                () => console.log('Login call complete')
            );
    }

    getUser() {
        var headers = new Headers();
        //headers.append('Content-Type', 'application/json');
        headers.append('Authorization', 'Bearer ' + this.accessToken);
        this._http.get("http://sr:8080/user", {headers: headers})
            .subscribe(
                response => {
                    console.log(response);
                    this.user = response.json();
                },
                error => console.error(error),
                () => console.log('User details call complete')
            )
    }
}