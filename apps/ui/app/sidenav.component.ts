import {View, Component} from 'angular2/core';
import {MATERIAL_DIRECTIVES, Media, SidenavService} from 'ng2-material/all';

@Component({
    selector: 'sr-sidenav',
    providers: [SidenavService],
    inputs: ['name'],
})
@View({
    template: `
<md-sidenav-container layout="row" flex>
  <md-sidenav [name]="name" [style]="'left'">
    <md-toolbar class="md-theme-indigo">
      <div class="md-toolbar-tools">
        <h1>Sidenav</h1>
        <span flex></span>
        <button md-button class="md-icon-button" aria-label="Close Side Panel" (click)="close()">
          <i md-icon>close</i>
        </button>
      </div>
    </md-toolbar>
    <md-content layout-padding>
    Sidenav Content
    </md-content>
  </md-sidenav>
</md-sidenav-container>
    `,
    directives: [MATERIAL_DIRECTIVES],
})
export class SidenavComponent {
    private name: string;

    constructor(public sidenav: SidenavService) {
        this.name = 'name';
    }

    open() {
        this.sidenav.show(this.name);
    }

    close() {
        this.sidenav.hide(this.name);
    }
}