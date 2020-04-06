import { Component } from '@angular/core';

import { BarService } from './bar.service';
import { DefaultRequestServerLogSupport } from '../serverlog/request-serverlog-support';
import { RequestServerLogCallWrapper } from '../serverlog/request-serverlog-call-wrapper';

@Component({
  selector: 'app-bar',
  templateUrl: './bar.component.html'
})
export class AppBarComponent {

    requestServerLogSupport = new DefaultRequestServerLogSupport();
    cw = new RequestServerLogCallWrapper(this.requestServerLogSupport);

    constructor(private barSvc: BarService) {
    }
    
    onClickHello() {
        this.barSvc.hello(this.cw).subscribe(res => {
            console.log('.. done Hello');
        }, err => {
            console.log('Failed Hello');
        });
    }

    onClickRepeatHello() {
        this.barSvc.repeatHello(this.cw).subscribe(res => {
            console.log('.. done Repeat Hello');
        }, err => {
            console.log('Failed Repeat Hello');
        });
    }

    onClickRepeatFastHello() {
        this.barSvc.repeatFastHello(this.cw).subscribe(res => {
            console.log('.. done Repeat Fast Hello');
        }, err => {
            console.log('Failed Repeat Fast Hello');
        });
    }

    onClickRepeatSlowHello() {
        this.barSvc.repeatSlowHello(this.cw).subscribe(res => {
            console.log('.. done Repeat Slow Hello');
        }, err => {
            console.log('Failed Repeat Slow Hello');
        });
    }

}
