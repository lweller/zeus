import {Component, OnInit} from '@angular/core';
import {trigger, state, transition, style, animate} from '@angular/animations';
import {Message, STATE_DONE} from '../../model/message';
import {MessageService} from '../../service/message.service';

@Component({
  selector: 'app-message-box',
  templateUrl: './message-box.component.html',
  styleUrls: ['./message-box.component.css'],
  animations: [
    trigger('visibility-changed', [
      state('shown', style({opacity: 1})),
      state('hidden', style({opacity: 0})),
      transition('hidden <=> shown', animate('1.5s 50ms ease-in-out')),
    ])
  ]
})
export class MessageBoxComponent implements OnInit {

  currentMessage: Message;

  constructor(private messageService: MessageService) {}

  ngOnInit() {
    this.messageService.getCurrentMessage().
      subscribe(currentMessage => this.currentMessage = currentMessage);
  }

  getVisibility() {
    return this.currentMessage.state === STATE_DONE ? 'hidden' : 'shown';
  }
}
