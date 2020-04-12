
import { Component } from '@angular/core';
import { ICellRendererAngularComp } from 'ag-grid-angular';
import { ICellRendererParams, IAfterGuiAttachedParams } from 'ag-grid-community';

@Component({
  selector: 'app-button-renderer',
  template: `
    <button type="button" (click)="onClick($event)">{{label}}</button>
    `
})
export class ButtonRendererComponent implements ICellRendererAngularComp {

  params;
  label: string;

  agInit(params): void {
    this.params = params;
    this.label = this.params.label || null;
  }

  refresh(params?: any): boolean {
    return true;
  }

  onClick($event) {
    if (this.params.onClick instanceof Function) {
      // put anything into params u want pass into parents component
      const clickParams = {
        event: $event,
        rowData: this.params.node.data,
		rendererParams: this.params
        // ...something
      }
      this.params.onClick(clickParams);

    }
  }
}