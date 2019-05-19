import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { RequestServerLogCallWrapper } from '../serverlog/request-serverlog-call-wrapper';

@Injectable()
export class BarService {
    
    private baseServerUrl = 'http://localhost:8080/api'

    private headers: HttpHeaders;
    private httpOptions: {};

    
    constructor(
        private httpClient: HttpClient
        ) {
        this.headers = new HttpHeaders({
            'Content-Type':  'application/json',
            'Accept':  'application/json',
            //.. cf Http interceptor.. 'Authorization': this.authService.getAuthorizationToken()
          });
        this.httpOptions = { headers: this.headers };
    }
    
    hello(cw: RequestServerLogCallWrapper): Observable<any> {
        return cw.wrapCall('Hello',
                (opts) => this.httpClient.get(this.baseServerUrl + '/bar/hello', opts), this.httpOptions);
    }

    repeatHello(cw: RequestServerLogCallWrapper): Observable<any> {
        return cw.wrapCall('Repeat Hello',
                (opts) => this.httpClient.get(this.baseServerUrl + '/bar/repeatHello', opts), this.httpOptions);
    }

    repeatSlowHello(cw: RequestServerLogCallWrapper): Observable<any> {
        return cw.wrapCall('Repeat Slow Hello',
                (opts) => this.httpClient.get(this.baseServerUrl +'/bar/repeatSlowHello', opts), this.httpOptions);
    }

    repeatFastHello(cw: RequestServerLogCallWrapper): Observable<any> {
        return cw.wrapCall('Repeat Fast Hello',
                (opts) => this.httpClient.get(this.baseServerUrl +'/bar/repeatFastHello', opts), this.httpOptions);
    }

}