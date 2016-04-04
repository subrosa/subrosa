import {Component, View, OnInit} from "angular2/core";
import {
    CORE_DIRECTIVES, FormBuilder, Validators, ControlGroup, FORM_DIRECTIVES, FORM_BINDINGS,
    FORM_PROVIDERS, Control
} from "angular2/common";
import {MATERIAL_DIRECTIVES} from "ng2-material/all";
import {SettingsService} from "./settings.service";
import {Router, ROUTER_PROVIDERS} from "angular2/router";

@Component({
    providers: [FORM_PROVIDERS, ROUTER_PROVIDERS],
})
@View({
    template: `
<md-content layout-padding>
<p>{{ apiRoot }}</p>
  <form [ngFormModel]="settingsForm" (submit)="onFormSubmit($event)">
    <md-input-container class="md-block">
      <label>API Root</label>
      <input md-input ngControl="apiRoot" type="url">
    </md-input-container>
    <button md-raised-button class="md-primary" aria-label="Save">Save</button>
  </form>
</md-content>
    `,
    directives: [CORE_DIRECTIVES, FORM_DIRECTIVES, MATERIAL_DIRECTIVES],
})
export class SettingsComponent implements OnInit {
    settingsForm: ControlGroup;
    apiRootControl: Control;

    constructor(private _fb: FormBuilder,
                private _settings: SettingsService,
                private _router: Router) {
        this.apiRootControl = new Control('', Validators.required);
        this.settingsForm = this._fb.group({
            apiRoot: this.apiRootControl,
        });
    }

    ngOnInit() {
        this._settings.apiRoot().subscribe(apiRoot => {
            console.log('settings.component', apiRoot);
            this.apiRootControl.updateValue(apiRoot);
        });
    }

    onFormSubmit(event) {
        event.preventDefault();
        this._settings.setApiRoot(this.settingsForm.value.apiRoot);
    }

}