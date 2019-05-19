import { Component } from '@angular/core';

import { FooService } from './foo.service';
import { DefaultRequestServerLogSupport } from '../serverlog/request-serverlog-support';
import { RequestServerLogCallWrapper } from '../serverlog/request-serverlog-call-wrapper';

@Component({
  selector: 'app-foo',
  templateUrl: './foo.component.html'
})
export class AppFooComponent {

    requestServerLogSupport = new DefaultRequestServerLogSupport();
    cw = new RequestServerLogCallWrapper(this.requestServerLogSupport);

    constructor(private fooSvc: FooService) {
    }
    
    onClickHello() {
        this.fooSvc.hello(this.cw).subscribe(res => {
            console.log('.. done Hello');
        }, err => {
            console.log('Failed Hello');
        });
    }

    onClickRepeatHello() {
        this.fooSvc.repeatHello(this.cw).subscribe(res => {
            console.log('.. done Repeat Hello');
        }, err => {
            console.log('Failed Repeat Hello');
        });
    }

    onClickRepeatFastHello() {
        this.fooSvc.repeatFastHello(this.cw).subscribe(res => {
            console.log('.. done Repeat Fast Hello');
        }, err => {
            console.log('Failed Repeat Fast Hello');
        });
    }

    onClickRepeatSlowHello() {
        this.fooSvc.repeatSlowHello(this.cw).subscribe(res => {
            console.log('.. done Repeat Slow Hello');
        }, err => {
            console.log('Failed Repeat Slow Hello');
        });
    }

}
