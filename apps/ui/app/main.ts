import {bootstrap} from 'angular2/platform/browser'
import {HTTP_PROVIDERS} from "angular2/http";
import {ROUTER_PROVIDERS} from "angular2/router";
import {AppComponent} from './app.component'
import {AuthService} from "./user/auth.service";

bootstrap(AppComponent, [AuthService, HTTP_PROVIDERS, ROUTER_PROVIDERS]);

