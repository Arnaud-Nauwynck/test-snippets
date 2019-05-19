
export interface RequestServerLogSupport {
    startRequest(displayMsg: string, traceId: string);
    endRequestOk(displayMsg: string, millis: number);
    endRequestError(displayMsg: string, millis: number, err: any, errAsText: string);
}

export type RequestStatus = 'notStarted' | 'started' | 'finished' | 'failed';

export class DefaultRequestServerLogSupport implements RequestServerLogSupport {

    status: RequestStatus = 'notStarted';
    
    traceId: string = null;
    
    displayMsg: string = null;
    
    millis: number = 0;
    err: any = null;
    errAsText: string = null;
    
    clear() {
        this.status = 'notStarted';
        this.traceId = null;
        this.displayMsg = null;
        this.millis = 0;
        this.err = null;
        this.errAsText = null;
    }

    startRequest(displayMsg: string, traceId: string) {
        this.status = 'started' 
        this.displayMsg = displayMsg;
        this.traceId = traceId;
        this.millis = 0;
        this.err = null;
        this.errAsText = null;
    }
    
    endRequestOk(displayMsg: string, millis: number) {
        this.status = 'finished';
        this.displayMsg = displayMsg;
        this.millis = millis;
        this.err = null;
        this.errAsText = null;
    }
    
    endRequestError(displayMsg: string, millis: number, err: any, errAsText: string) {
        this.status = 'failed';
        this.displayMsg = displayMsg;
        this.millis = millis;
        this.err = err;
        this.errAsText = errAsText;
    }

}
