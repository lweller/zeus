import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
    selector: 'app-editable-label',
    templateUrl: './editable-label.component.html',
    styleUrls: ['./editable-label.component.css']
})
export class EditableLabelComponent implements OnInit {

    @Input() value = '';

    @Output() startedEditing = new EventEmitter();
    @Output() canceledEditing = new EventEmitter();
    @Output() finishedEditing = new EventEmitter<String>();

    editing = false;

    constructor() {
    }

    static setCaretPosition(inputField, caretStartPos, caretEndPos) {
        if (inputField) {
            inputField.focus();
            inputField.setSelectionRange(caretStartPos, caretEndPos);
        }
    }

    ngOnInit() {
    }

    startEditing(inputField) {
        if (!this.editing) {
            this.editing = true;
            EditableLabelComponent.setCaretPosition(inputField, 0, inputField.value.length);
            this.startedEditing.emit();
        }
    }

    cancelEditing(inputField) {
        if (this.editing) {
            inputField.value = this.value;
            this.editing = false;
            this.canceledEditing.emit();
        }
    }

    finishEditing(inputField) {
        if (this.editing) {
            this.editing = false;
            this.value = inputField.value;
            EditableLabelComponent.setCaretPosition(inputField, 0, 0);
            this.finishedEditing.emit(this.value);
        }
    }
}
