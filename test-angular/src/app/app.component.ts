import { Component, OnInit } from '@angular/core';

import { FooService } from './swagger-generated/api/api';
import { FooRequest, FooResponse } from './swagger-generated/model/models';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  
})
export class AppComponent implements OnInit {

  showFooter: boolean = false;
  textResults = "";

  constructor(private fooSvc: FooService) {
  }
  
  ngOnInit() {
  }

  onClickShow() {
      this.showFooter = true;
  }
  onClickHide() {
      this.showFooter = false;
  }
  
  onClickShowHide() {
      this.showFooter = true;
      setTimeout(() => {
          this.showFooter = false;
          
      }, 4000)
  }
  
  onClick_getFoo() {
      this.fooSvc.getFooUsingGET().subscribe(res => {
          console.log('.. getFoo', res);
      }, err => {
          console.error('Failed ..getFoo', err);
      });
  }

  onClick_getText() {
      this.fooSvc.getTextUsingGET().subscribe(res => {
          console.log('.. getText', res);
          this.textResults += 'getText => ' + res + '\n';
      }, err => {
          console.error('Failed ..getText', err);
          this.textResults += 'Failed getText => ' + err.error + '\n';
      });
  }

  onClick_getJsonText() {
      this.fooSvc.getJsonTextUsingGET().subscribe(res => {
          console.log('.. getJsonText', res);
          this.textResults += 'getJsonText => ' + res + '\n';
      }, err => {
          console.error('Failed ..getJsonText', err);
          this.textResults += 'Failed getJsonText => ' + err.error + '\n';
      });
  }

}
