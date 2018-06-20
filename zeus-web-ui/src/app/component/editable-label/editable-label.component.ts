import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';

@Component({
  selector: 'app-editable-label',
  templateUrl: './editable-label.component.html',
  styleUrls: ['./editable-label.component.css']
})
export class EditableLabelComponent implements OnInit {

  @Input() value: string;

  @Output() startedEditing = new EventEmitter();
  @Output() canceledEditing = new EventEmitter();
  @Output() finishedEditing = new EventEmitter<String>();

  editing: boolean;

  constructor() {}

  ngOnInit() {
    this.editing = false;
  }

  startEditing(inputfield) {
    if (!this.editing) {
      this.editing = true;
      this.setCaretPosition(inputfield, 0, inputfield.value.length);
      this.startedEditing.emit();
    }
  }

  cancelEditing(inputfield) {
    if (this.editing) {
      inputfield.value = this.value;
      this.editing = false;
      this.canceledEditing.emit();
    }
  }

  finishEditing(inputfield) {
    if (this.editing) {
      this.editing = false;
      this.value = inputfield.value;
      this.setCaretPosition(inputfield, 0, 0);
      this.finishedEditing.emit(this.value);
    }
  }

  setCaretPosition(elem, caretStartPos, caretEndPos) {
    if (elem !== null) {
      if (elem.createTextRange) {
        const range = elem.createTextRange();
        range.move('character', caretStartPos, caretEndPos);
        range.select();
      } else {
        if (elem.setSelectionRange) {
          elem.focus();
          elem.setSelectionRange(caretStartPos, caretEndPos);
        }
      }
    }
  }
}
