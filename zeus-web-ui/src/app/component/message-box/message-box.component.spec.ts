import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageFieldComponent } from './message-field.component';

describe('MessageFieldComponent', () => {
  let component: MessageFieldComponent;
  let fixture: ComponentFixture<MessageFieldComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MessageFieldComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
