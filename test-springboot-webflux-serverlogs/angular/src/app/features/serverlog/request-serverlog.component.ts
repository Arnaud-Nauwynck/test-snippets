import { Component, Input } from '@angular/core';

import { RequestServerLogSupport } from './request-serverlog-support';

@Component({
  selector: 'app-request-serverlog',
  templateUrl: './request-serverlog.component.html'
})
export class AppRequestServerLogComponent {

  showDetailed: boolean = true;

  @Input()
  model: RequestServerLogSupport = null;

}
