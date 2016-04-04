import {Component} from "angular2/core";
import {MATERIAL_DIRECTIVES} from "ng2-material/all";
import {CORE_DIRECTIVES} from "angular2/common";

@Component({
    template: `
<md-whiteframe class="md-whiteframe-1dp" flex="100" flex-gt-lg="50" layout="column">
  <md-content layout-padding>
    <h3>Dashboard</h3>
  </md-content>
</md-whiteframe>
    `,
    directives: [CORE_DIRECTIVES, MATERIAL_DIRECTIVES],
})
export class DashboardComponent {
}