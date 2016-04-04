import {bootstrap} from "angular2/platform/browser";
import {HTTP_PROVIDERS} from "angular2/http";
import {ROUTER_PROVIDERS} from "angular2/router";
import {AppComponent} from "./app.component";
import {AuthService} from "./user/auth.service";
import {SettingsService} from "./settings/settings.service";

bootstrap(AppComponent, [
    AuthService, SettingsService,
    HTTP_PROVIDERS, ROUTER_PROVIDERS,
]);

