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
      <a [routerLink]="['Home']">
        <h2><span>SR</span></h2>
      </a>
      <span flex></span>
      <button md-button class="md-icon-button" aria-label="Settings" [routerLink]="['Settings']">
        <i md-icon>settings</i>
      </button>
      <button md-button class="md-icon-button" aria-label="User Details" [routerLink]="['Whoami']">
        <i md-icon>account_circle</i>
      </button>
    </div>
  </md-toolbar>
    `,
    directives: [ROUTER_DIRECTIVES, MATERIAL_DIRECTIVES],
})
export class HeaderComponent {
}