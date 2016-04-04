import {Component, View, OnInit} from "angular2/core";
import {CORE_DIRECTIVES, FormBuilder, Validators, ControlGroup, FORM_DIRECTIVES} from "angular2/common";
import {User} from "./user";
import {AuthService} from "./auth.service";
import {MATERIAL_DIRECTIVES} from "ng2-material/all";
import {Client} from "./client";

@Component({
    inputs: ['user'],
})
@View({
    templateUrl: 'app/user/user-details.component.html',
    directives: [CORE_DIRECTIVES, FORM_DIRECTIVES, MATERIAL_DIRECTIVES],
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