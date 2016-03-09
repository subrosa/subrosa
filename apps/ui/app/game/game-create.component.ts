import {Component} from 'angular2/core';
import {RouteConfig, RouterOutlet} from 'angular2/router';

@Component({
    template:  `
    <p>Create</p>
  `,
    directives: [RouterOutlet]
})
export class GameCreateComponent {
}