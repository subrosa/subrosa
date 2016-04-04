import {Component} from "angular2/core";
import {ROUTER_DIRECTIVES, RouteConfig, Route} from "angular2/router";
import {MATERIAL_DIRECTIVES} from "ng2-material/all";
import {HeaderComponent} from "./header.component";
import {SidenavComponent} from "./sidenav.component";
import {FooterComponent} from "./footer.component";
import {CORE_DIRECTIVES} from "angular2/common";
import {DashboardComponent} from "./dashboard.component";
import {SettingsComponent} from "./settings/settings.component";
import {UserDetailsComponent} from "./user/user-details.component";
import {GameComponent} from "./game/game.component";
import {AccountComponent} from "./account/account.component";

@Component({
    selector: 'subrosa-app',
    directives: [CORE_DIRECTIVES, MATERIAL_DIRECTIVES, ROUTER_DIRECTIVES, HeaderComponent, SidenavComponent, FooterComponent],
    template: `
<sr-header [sidenav]="sidenav"></sr-header>
<sr-sidenav #sidenav [name]="left"></sr-sidenav>
<md-content layout-padding layout="column">
  <router-outlet></router-outlet>
</md-content>
<sr-footer></sr-footer>
    `,
    styles: [`
.body {
    margin-top: 50px;
}
    `],
})
@RouteConfig([
    new Route({path: '/', name: 'Home', component: DashboardComponent}),
    new Route({path: '/whoami', name: 'Whoami', component: UserDetailsComponent}),
    new Route({path: '/settings', name: 'Settings', component: SettingsComponent}),
    new Route({path: '/game/...', name: 'Game', component: GameComponent}),
    new Route({path: '/account/...', name: 'Account', component: AccountComponent}),
])
export class AppComponent {
}