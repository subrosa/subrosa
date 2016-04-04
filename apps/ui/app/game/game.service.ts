import {Injectable} from "angular2/core";
import {Observable} from "rxjs/Observable";
import {Game} from "./game";
import {Http} from "angular2/http";
import {AuthService} from "../user/auth.service";
import {Observer} from "rxjs/Observer";
import {SettingsService} from "../settings/settings.service";

@Injectable()
export class GameService {

    games$: Observable<Array<Game>>;
    // error$: Observable<Error>;
    private _gamesObserver: Observer<Array<Game>>;

    constructor(private _http: Http,
                private _auth: AuthService,
                private _settings: SettingsService) {
        this.games$ = new Observable(obs => this._gamesObserver = obs).share();
    }

    loadGames() {
        console.log('loadGames');
        this._auth.clientTokenHeaders().withLatestFrom(this._settings.apiRoot()).subscribe(inputs => {
            let headers = inputs[0];
            let apiRoot = inputs[1];
            this._http.get(`${apiRoot}/subrosa/v1/game`, {headers: headers})
                .map(response => response.json())
                .subscribe(data => {
                    console.log(data);
                    this._gamesObserver.next(data.results);
                }, error => console.error('Failed to load games.', error));
        });
    }
}