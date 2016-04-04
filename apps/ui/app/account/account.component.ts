import {Component} from "angular2/core";
import {RouteConfig, RouterOutlet} from "angular2/router";
import {AccountListComponent} from "./account-list.component";
import {AccountCreateComponent} from "./account-create.component";
import {AccountService} from "./account.service";

@Component({
    template: `
    <router-outlet></router-outlet>
  `,
    directives: [RouterOutlet],
    providers: [AccountService],
})
@RouteConfig([
    {
        path: '/',
        name: 'AccountList',
        component: AccountListComponent,
        useAsDefault: true
    },
    {
        path: '/create',
        name: 'AccountCreate',
        component: AccountCreateComponent
    }
])
export class AccountComponent {
}