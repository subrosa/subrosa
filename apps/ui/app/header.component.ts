import {Component} from 'angular2/core';

@Component({
    selector: 'sr-header',
    templateUrl: 'app/header.component.html',
})
export class HeaderComponent {
    public context = 'Dashboard';
}
