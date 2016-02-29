import {Component} from 'angular2/core';
import {RouteConfig, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from 'angular2/router';
import {User} from "./user/user";
import {HeaderComponent} from "./header.component";
import {SidenavComponent} from "./sidenav.component";
import {ContentComponent} from "./content.component";
import {FooterComponent} from "./footer.component";
import {UserDetailsComponent} from "./user/user-details.component";

@Component({
    selector: 'subrosa-app',
    directives: [ROUTER_DIRECTIVES, HeaderComponent, SidenavComponent, ContentComponent, FooterComponent],
    providers: [
        ROUTER_PROVIDERS,
    ],
    template: `
<!--<sr-header></sr-header>-->
<nav class="navbar navbar-dark navbar-fixed-top bg-inverse">
  <a class="navbar-brand" href="#">SR</a>
  <div id="navbar pull-right">
    <nav class="nav navbar-nav pull-xs-right">
      <a class="nav-item nav-link" [routerLink]="['Whoami']">.whoami</a>
    </nav>
  </div>
</nav>
<div class="body">
<div class="container-fluid">
<div class="row">
  <!--<sr-sidenav></sr-sidenav>-->
  <router-outlet></router-outlet>
</div>
</div>
</div>
<sr-footer></sr-footer>
    `,
    styles: [`
.body {
    margin-top: 50px;
}
    `],
})
@RouteConfig([
    {
        path: '/whoami',
        name: 'Whoami',
        component: UserDetailsComponent
    },
])
export class AppComponent {
    public user: User = {
        id: 1,
        username: 'User-face',
        email: 'test@example.com',
        phone: '',
    };
    public context = 'Dashboard';
}