import { Component, Input } from '@angular/core';
import {
  transition,
  trigger,
  state,
  style,
  animate,
} from '@angular/animations';



@Component({
  selector: 'app-bottompanel',
  templateUrl: './bottompanel.component.html',
  styleUrls: ['./bottompanel.component.css'],
  
  animations: [
   trigger('openClose', [
     
     state('open', style({
         'max-height': '200px',
         height: 'auto',
     })),
     
     state('closed', style({
         'max-height': '5px',
         height: '5px',
     })),

     transition('open => closed', [
       animate('3s linear')
     ]),
     transition('closed => open', [
       animate('1s')
     ]),
   ]), // trigger
  ], // animations
})
export class BottomPanelComponent {

  @Input()
  showFooter: boolean = false;

  @Input()
  progressMsg: string = null;
  @Input()
  errorMsg: string = null;

  onAnimationStart(event: any) {
      console.log('onAnimationStart');
  }

  onAnimationDone(event: any) {
      console.log('onAnimationDone');
  }

}
