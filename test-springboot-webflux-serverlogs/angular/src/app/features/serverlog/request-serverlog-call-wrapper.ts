import { HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, catchError} from 'rxjs/operators';
import * as uuid from 'uuid';

import { RequestServerLogSupport } from './request-serverlog-support';

interface HttpOptions {
    headers?: HttpHeaders | {
        [header: string]: string | string[];
    };
    observe?: 'body';
    params?: HttpParams | {
        [param: string]: string | string[];
    };
    reportProgress?: boolean;
    responseType?: 'json';
    withCredentials?: boolean;
}

export class RequestServerLogCallWrapper {

    constructor(private cb: RequestServerLogSupport) {
    }

    // ------------------------------------------------------------------------
    
    static call<T>(cb: RequestServerLogSupport, displayMsg: string, callFunc: (HttpOptions) => Observable<T>, httpOptions: HttpOptions): Observable<T> {
        return new RequestServerLogCallWrapper(cb).wrapCall(displayMsg, callFunc, httpOptions);
    }
    
    wrapCall<T>(displayMsg: string, 
            callFunc: (HttpOptions) => Observable<T>,
            httpOptions: HttpOptions
            ): Observable<T> {
        let startMillis = Date.now();
        let traceId = uuid.v4();
        let opts = { ...httpOptions };
//        if (opts.headers.prototype === HttpHeaders.prototype) {
            opts.headers = (<HttpHeaders> opts.headers).append('trace-id', traceId);
//        } else {
//            opts.headers = { ...opts.headers, 'trace-id', traceId}
//        }
        this.handleStartRequest(displayMsg, traceId);
        return callFunc(opts).pipe(
            map(data => this.handleSuccess(data, displayMsg, startMillis)),
            catchError(error => { this.handleRethrowError(error, displayMsg, startMillis); throw error; })
            );
    }


    private handleStartRequest(displayMsg: string, traceId: string) {
        try {
            this.cb.startRequest(displayMsg, traceId);
        } catch(ex) {
            console.log('Failed startRequest in serverLog..ignore', ex);
        }
    }
    
    private handleSuccess<T>(data: T, displayMsg: string, startTime: number): T {
        let millis = Date.now() - startTime;
        try {
            this.cb.endRequestOk(displayMsg, millis);
        } catch(ex) {
            console.log('Failed handleSuccess in serverLog..ignore', ex);
        }
        return data;
    }
    
    private handleRethrowError(ex: any, displayMsg: string, startTime: number) {
        let millis = Date.now() - startTime;
        let errAsText = 'Error'; // TODO err
        console.log('ex', ex)
        if (ex['message']) {
            let exMessage = ex['message'];
            if (typeof exMessage === 'string') {
                errAsText = exMessage;
            }
        }
        
        try {
            this.cb.endRequestError(displayMsg, millis, ex, errAsText);
        } catch(ex2) {
            console.log('Failed catchError in serverLog..ignore', ex2);
        }
    }
    
}
