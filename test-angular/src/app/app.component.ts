import { Component, OnInit } from '@angular/core';

import { FooService } from './swagger-generated/api/api';
import { FooRequest, FooResponse } from './swagger-generated/model/models';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  
})
export class AppComponent implements OnInit {

  textResults = "";

  showFooter: boolean = false;
  progressMsg: string = null;
  errorMsg: string = null;
  
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
          this.logAndFormatError('getJsonText', err);
      });
  }

  onClick_getFooSlow() {
      this.progressMsg = 'getFooSlow';
      this.errorMsg = null;
      this.showFooter = true;
      this.fooSvc.getFooSlowUsingGET().subscribe(res => {
          this.showFooter = false;
          this.progressMsg = null;
          console.log('.. getFooSlow', res);
          this.textResults += 'getFooSlow => ' + res + '\n';
      }, err => {
          this.showFooter = false;
          this.progressMsg = null;
          this.logAndFormatError('getFooSlow', err);
      });
  }

  onClick_getFoo4xx() {
      this.progressMsg = 'getFoo4xx';
      this.errorMsg = null;
      this.showFooter = true;
      this.fooSvc.getFoo4xxUsingGET().subscribe(res => {
          this.progressMsg = null;
          this.showFooter = false;
          console.log('.. getFoo4xx', res);
          this.textResults += 'getFoo4xx => ' + res + '\n';
      }, err => {
          this.progressMsg = null;
          this.logAndFormatError('getFoo4xx', err);
      });
  }

  onClick_getFooFailed5xx() {
      this.progressMsg = 'getFoo5xx';
      this.errorMsg = null;
      this.showFooter = true;
      this.fooSvc.getFoo5xxUsingGET().subscribe(res => {
          this.progressMsg = null;
          this.showFooter = false;
          console.log('.. getFooFailed5xx', res);
          this.textResults += 'getFooFailed5xx => ' + res + '\n';
      }, err => {
          this.progressMsg = null;
          this.showFooter = true;
          this.logAndFormatError('getFooFailed5xx', err);
      });
  }
  
  logAndFormatError(displayText: string, err: any) {
      var errText = '';
      if (err.message) {
          errText += ' ' + err.message;
      } else {
          if (err.status) {
              errText += ' status: ' + err.status;
          }
      }
      // maybe an exception error..
      if (err.error) {
          if (err.error.message) {
              errText += ' - exception: ' + err.error.message;
          }
      }
      
      console.error('Failed .. ' + displayText + ' ' + errText, err);
      this.textResults += 'Failed ' + displayText + ' => ' + errText + '\n';

      this.errorMsg = 'Failed ' + displayText + ' => ' + errText + '\n';

  }
}
