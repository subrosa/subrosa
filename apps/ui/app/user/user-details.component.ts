import {Component} from 'angular2/core';
import {CORE_DIRECTIVES} from "angular2/common";
import {Http, Headers} from 'angular2/http';
import {User} from "app/user/user";
import {Login} from "app/user/login";
import {AuthService} from "./auth.service";
import {View} from "angular2/core";
import {MATERIAL_DIRECTIVES} from "ng2-material/all";
import {Client} from "./client";
import {OnInit} from "angular2/core";
import {FormBuilder} from "angular2/common";
import {Validators} from "angular2/common";
import {ControlGroup} from "angular2/common";

@Component({
    providers: [AuthService],
    inputs: ['user'],
})
@View({
    templateUrl: 'app/user/user-details.component.html',
    directives: [CORE_DIRECTIVES, MATERIAL_DIRECTIVES],
})
export class UserDetailsComponent implements OnInit {
    client: Client;
    clientForm: ControlGroup;
    user: User;
    loginForm: ControlGroup;

    constructor(private _fb: FormBuilder,
                private _auth: AuthService) {
        this.clientForm = this._fb.group({
            clientId: ["", Validators.required],
            secret: ["", Validators.required],
        });
        this.loginForm = this._fb.group({
            email: ["", Validators.required],
            password: ["", Validators.required],
        });
    }

    ngOnInit() {
        this._auth.user.subscribe(
            (user: User) => this.user = user
        );
        this._auth.client.subscribe(
            (client: Client) => this.client = client
        );
    }

    logoutClient() {
        this._auth.resetClient();
    }

    onClientSubmit(event) {
        event.preventDefault();
        this._auth.updateClientCredentials(this.clientForm.value);
        this._auth.clientCredentialsGrant();
    }

    logout() {
        this._auth.resetUser();
    }

    onLoginSubmit(event) {
        event.preventDefault();
        this._auth.passwordGrant(this.loginForm.value);
    }

}