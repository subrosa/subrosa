import {Injectable} from "angular2/core";
import {Observable} from "rxjs/Observable";
import {Game} from "./game";
import {Http} from "angular2/http";
import {AuthService} from "../user/auth.service";
import {Observer} from "rxjs/Observer";

@Injectable()
export class GameService {

    games$: Observable<Array<Game>>;
    private _gamesObserver: Observer;

    constructor(private _http: Http,
                private _auth: AuthService) {
        this.games$ = new Observable(obs => this._gamesObserver = obs).share();
    }

    loadGames() {
        this._auth.clientTokenHeaders().subscribe(headers => {
            this._http.get("http://sr:8080/subrosa/v1/game", {headers: headers})
                .map(response => response.json())
                .subscribe(data => {
                    this._gamesObserver.next(data.results);
                }, error => console.error('Failed to load games.', error));
        });
    }
}