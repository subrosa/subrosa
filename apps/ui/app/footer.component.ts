import {Component, Inject, View, OnInit} from 'angular2/core';
import {SettingsService} from "./settings/settings.service";

@Component({
    selector: 'sr-footer',
})
@View({
    template: `
<div class="footer" layout="row" layout-padding>
  <span class="md-caption">{{ apiRoot }}</span>
  <span flex></span>
  <span class="md-caption">Your mom is &copy; 2016</span>
</div>
    `,
    styles: [`
.footer {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 60px;
  line-height: 60px;
  background-color: #f5f5f5;
}
    `]
})
export class FooterComponent implements OnInit {
    apiRoot: string;
    
    constructor(@Inject(SettingsService) private _settings: SettingsService) {
    }
    
    ngOnInit() {
        this._settings.apiRoot().subscribe(apiRoot => {
            console.log('footer.component', apiRoot);
            this.apiRoot = apiRoot;
        });
    }
}
