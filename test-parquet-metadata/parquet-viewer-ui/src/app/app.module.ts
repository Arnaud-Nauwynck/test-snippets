import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AgGridModule } from 'ag-grid-angular';

import { AppComponent } from './app.component';
import { ButtonRendererComponent } from './renderer/button-renderer.component';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
	FormsModule, ReactiveFormsModule,
    AgGridModule.withComponents([
		ButtonRendererComponent
	])
  ],
  providers: [
	ButtonRendererComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
    