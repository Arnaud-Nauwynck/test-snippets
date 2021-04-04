import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AgGridModule } from 'ag-grid-angular';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ButtonRendererComponent } from './renderer/button-renderer.component';

import { ApiModule } from './ext';
import { Configuration } from './ext/configuration';

let apiConfiguration = new Configuration({
	basePath: 'http://localhost:8080'
});


@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
	HttpClientModule,
    AppRoutingModule,
    FormsModule, ReactiveFormsModule,
    AgGridModule.withComponents([
		ButtonRendererComponent
	]),
	ApiModule.forRoot(() => apiConfiguration)
  ],
  providers: [
  	ButtonRendererComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
