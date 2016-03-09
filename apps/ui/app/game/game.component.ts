import {Component} from 'angular2/core';
import {RouteConfig, RouterOutlet} from 'angular2/router';
import {GameListComponent} from "./game-list.component";
import {GameCreateComponent} from "./game-create.component";
import {GameService} from "./game.service";

@Component({
    template:  `
    <router-outlet></router-outlet>
  `,
    directives: [RouterOutlet],
    providers: [GameService],
})
@RouteConfig([
    {
        path: '/',
        name: 'GameList',
        component: GameListComponent,
        useAsDefault: true
    },
    {
        path: '/create',
        name: 'GameCreate',
        component: GameCreateComponent
    }
])
export class GameComponent {
}