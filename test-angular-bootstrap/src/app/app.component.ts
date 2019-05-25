import { Component, OnInit } from '@angular/core';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  
})
export class AppComponent implements OnInit {

  showFooter: boolean = false;

  constructor(){
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
}
