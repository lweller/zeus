import {TranslateService} from '@ngx-translate/core';
import {Component, OnInit} from '@angular/core';
import {trigger, state, transition, style, animate} from '@angular/animations';
import {Scenario} from '../../model/scenario';
import {ScenarioService} from '../../service/scenario.service';

@Component({
  selector: 'app-scenarios',
  templateUrl: './scenarios.component.html',
  styleUrls: ['./scenarios.component.css'],
  animations: [
    trigger('state-changed', [
      state('enabled', style({'background-color': '#4782e2', color: 'white'})),
      state('disabled', style({'background-color': '#c0c0c0', color: 'black'})),
      transition('enabled <=> disabled', animate('0.2s'))
    ])
  ]
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

  update(scenario: Scenario): void {
  }

  toggleEnabling(scenario: Scenario): void {
    this.scnearioService.toggleEnabling(scenario).subscribe(updatedScenario =>
      this.scenarios[this.scenarios.indexOf(scenario)] = updatedScenario);
  }

  getState(scenario: Scenario): string {
    return scenario.enabled ? 'enabled' : 'disabled';
  }
}