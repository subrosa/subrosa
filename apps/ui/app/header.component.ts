import {View, Component} from 'angular2/core';
import {MATERIAL_DIRECTIVES, Media, SidenavService} from 'ng2-material/all';
import {ROUTER_PROVIDERS, ROUTER_DIRECTIVES} from "angular2/router";
import {Router} from "angular2/router";

@Component({
    selector: 'sr-header',
    inputs: ['sidenav'],
})
@View({
    template: `
  <md-toolbar>
    <div class="md-toolbar-tools">
      <button md-button class="md-icon-button" aria-label="Menu" (click)="sidenav.open()">
        <i md-icon>menu</i>
      </button>
      <h2>
        <span>SR</span>
      </h2>
      <span flex></span>
      <button md-button class="md-icon-button" aria-label="Authentication" (click)="navigate(['Whoami'])">
        <i md-icon>account_circle</i>
      </button>
    </div>
  </md-toolbar>
    `,
    directives: [ROUTER_DIRECTIVES, MATERIAL_DIRECTIVES],
})
export class HeaderComponent {

    constructor(private _router: Router) {
    }

    navigate(route) {
        this._router.navigate(route);
    }
}