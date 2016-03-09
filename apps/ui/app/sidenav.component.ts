import {View, Component} from 'angular2/core';
import {MATERIAL_DIRECTIVES, Media, SidenavService} from 'ng2-material/all';
import {ROUTER_DIRECTIVES} from "angular2/router";
import {Router} from "angular2/router";

@Component({
    selector: 'sr-sidenav',
    providers: [SidenavService],
    inputs: ['name'],
})
@View({
    templateUrl: 'app/sidenav.component.html',
    styles: [`
    md-list-item {
        cursor: pointer;
    }
    `],
    directives: [ROUTER_DIRECTIVES, MATERIAL_DIRECTIVES],
})
export class SidenavComponent {
    private name: string;

    constructor(public sidenav: SidenavService,
                private _router: Router) {
    }

    open() {
        this.sidenav.show(this.name);
    }

    close() {
        this.sidenav.hide(this.name);
    }

    navigate(route) {
        this._router.navigate(route);
        this.close();
    }
}