import {TranslateService} from '@ngx-translate/core';
import {Component, OnInit} from '@angular/core';
import {Event} from '../../model/event';
import {EventService} from '../../service/event.service';

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {

  events: Event[];

  constructor(private translateService: TranslateService, private eventService: EventService) {}

  ngOnInit() {
    this.load();
  }

  load(): void {
    this.eventService.findAll().subscribe(events => this.events = events);
  }

  update(event: Event): void {
  }

  fire(event: Event): void {
    this.eventService.fire(event).subscribe(updatedEvent =>
      this.events[this.events.indexOf(event)] = updatedEvent);
  }

  buildNextOccurenceExpression(event: Event): string {
    const date = event.nextScheduledExecution;
    const secondsUntilNextFiring = (new Date(date).getTime() - new Date().getTime()) / 1000;
    let espression = '';
    let fragment;
    if (secondsUntilNextFiring < 60) {
      this.translateService.get('less than a minute')
        .subscribe(result => fragment = result);
      espression = fragment;
    } else if (secondsUntilNextFiring > 604800) {
      this.translateService.get('more than a week')
        .subscribe(result => fragment = result);
      espression = fragment;
    } else {
      const days = Math.floor(secondsUntilNextFiring / 86400);
      const hours = Math.floor((secondsUntilNextFiring % 86400) / 3600);
      const minutes = Math.floor((secondsUntilNextFiring % 3600) / 60);
      this.translateService.get('and')
        .subscribe(result => fragment = result);
      const andFragement = fragment;
      if (days > 1) {
        this.translateService.get('{days} days', {days: days})
          .subscribe(result => fragment = result);
        espression += fragment;
      } else if (days === 1) {
        this.translateService.get('one day')
          .subscribe(result => fragment = result);
        espression = fragment;
      }
      if (days > 0) {
        espression += minutes > 0 ? ' ' : ' ' + andFragement + ' ';
      }
      if (hours > 1) {
        this.translateService.get('{hours} hours', {hours: hours})
          .subscribe(result => fragment = result);
        espression += fragment;
      } else if (hours === 1) {
        this.translateService.get('1 hour')
          .subscribe(result => fragment = result);
        espression += fragment;
      }
      if (days > 0 || hours > 0) {
        espression += ' ' + andFragement + ' ';
      }
      if (minutes > 1) {
        this.translateService.get('{minutes} minutes', {minutes: minutes})
          .subscribe(result => fragment = result);
        espression += fragment;
      } else if (minutes === 1) {
        this.translateService.get('1 minute')
          .subscribe(result => fragment = result);
        espression += fragment;
      }
    }
    return espression;
  }
}
