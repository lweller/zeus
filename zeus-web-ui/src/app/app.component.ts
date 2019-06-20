import {MessageService} from './common/service/message.service';
import {Component} from '@angular/core';
import {Event, NavigationStart, Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {
    constructor(private translateService: TranslateService, private messageService: MessageService, private router: Router) {
        translateService.addLangs(['en', 'de', 'fr']);
        translateService.setDefaultLang('en');
        const browserLang = translateService.getBrowserLang();
        translateService.use(browserLang.match(/en|de|fr/) ? browserLang : 'en');
        router.events.subscribe((event: Event) => {
            if (event instanceof NavigationStart) {
                messageService.reset();
            }
        });
    }
}
