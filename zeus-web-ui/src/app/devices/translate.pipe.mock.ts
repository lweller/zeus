import {Pipe, PipeTransform} from '@angular/core';

// noinspection AngularMissingOrInvalidDeclarationInModule
@Pipe({name: 'translate'})
export class TranslatePipeMock implements PipeTransform {
    transform(value: any, ...args: any[]): any {
        return value;
    }
}
