import {Component} from 'angular2/core';
import {RouteConfig, RouterOutlet} from 'angular2/router';
import {GameService} from "./game.service";
import {Game} from "./game";
import {OnInit} from "angular2/core";
import {MATERIAL_DIRECTIVES} from "ng2-material/all";

@Component({
    templateUrl: 'app/game/game-list.component.html',
    providers: [GameService],
    directives: [MATERIAL_DIRECTIVES],
})
export class GameListComponent implements OnInit {
    games: Game[] = null;

    constructor(private _gameSvc: GameService) {
    }

    ngOnInit() {
        this._gameSvc.games$.subscribe(games => this.games = games);
        this._gameSvc.loadGames();
    }
}