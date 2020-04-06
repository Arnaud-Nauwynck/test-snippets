import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule }   from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AuthService } from './shared/auth-service';
import { AuthInterceptor } from './shared/auth-interceptor';

import { AppComponent } from './app.component';

import { AppFooComponent } from './features/foo/foo.component';
import { FooService } from './features/foo/foo.service';

import { AppBarComponent } from './features/bar/bar.component';
import { BarService } from './features/bar/bar.service';

import { AppServerLogComponent } from './features/serverlog/serverlog.component';
import { AppRequestServerLogComponent } from './features/serverlog/request-serverlog.component';

@NgModule({
  declarations: [
    AppComponent,
    AppServerLogComponent,
    AppRequestServerLogComponent,
    AppFooComponent,
    AppBarComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [
    AuthService,
    AuthInterceptor,
    FooService,
    BarService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
