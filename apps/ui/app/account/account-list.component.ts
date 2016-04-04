import {Component} from 'angular2/core';
import {AccountService} from "./account.service";
import {Account} from "./account";
import {OnInit} from "angular2/core";
import {MATERIAL_DIRECTIVES} from "ng2-material/all";

@Component({
    templateUrl: 'app/account/account-list.component.html',
    providers: [AccountService],
    directives: [MATERIAL_DIRECTIVES],
})
export class AccountListComponent implements OnInit {
    accounts: Account[] = null;

    constructor(private _accountSvc: AccountService) {
    }

    ngOnInit() {
        this._accountSvc.accounts$.subscribe(accounts => this.accounts = accounts);
        this._accountSvc.loadAccounts();
    }
}