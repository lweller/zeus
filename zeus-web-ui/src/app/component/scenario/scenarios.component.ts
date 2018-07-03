import {TranslateService} from '@ngx-translate/core';
import {Component, OnInit} from '@angular/core';
import {Scenario} from '../../model/scenario';
import {ScenarioService} from '../../service/scenario.service';

@Component({
  selector: 'app-scenarios',
  templateUrl: './scenarios.component.html',
  styleUrls: ['./scenarios.component.css']
})
export class ScenariosComponent implements OnInit {

  scenarios: Scenario[];

  constructor(private translateService: TranslateService, private scnearioService: ScenarioService) {}

  ngOnInit() {
    this.load();
  }

  load(): void {
    this.scnearioService.findAll().subscribe(scenarios => this.scenarios = scenarios);
  }

  update(event: Event): void {
  }
}
