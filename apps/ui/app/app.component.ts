import {Component} from 'angular2/core';
import {RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from 'angular2/router';
import {MATERIAL_DIRECTIVES, MATERIAL_PROVIDERS} from 'ng2-material/all';

import {UserDetailsComponent} from "./user/user-details.component";
import {DashboardComponent} from "./dashboard.component";
import {HeaderComponent} from "./header.component";
import {SidenavComponent} from "./sidenav.component";
import {FooterComponent} from "./footer.component";
import {GameComponent} from "./game/game.component";

@Component({
    selector: 'subrosa-app',
    directives: [ROUTER_DIRECTIVES, MATERIAL_DIRECTIVES, HeaderComponent, SidenavComponent, FooterComponent],
    template: `
<div layout="column">
    <sr-header [sidenav]="sidenav"></sr-header>
</div>
<div layout="column">
    <sr-sidenav #sidenav [name]="left"></sr-sidenav>
    <md-content>
    <router-outlet></router-outlet>
    </md-content>
</div>
<div layout="column">
    <sr-footer></sr-footer>
</div>
    `,
    styles: [`
.body {
    margin-top: 50px;
}
    `],
})
@RouteConfig([
    {
        path: '/',
        name: 'Home',
        component: DashboardComponent
    },
    {
        path: '/whoami',
        name: 'Whoami',
        component: UserDetailsComponent
    },
    {
        path: '/game/...',
        name: 'Game',
        component: GameComponent
    }
])
export class AppComponent {
}