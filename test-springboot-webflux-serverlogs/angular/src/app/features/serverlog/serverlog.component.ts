import { Component, OnInit, OnDestroy, Input, ViewChild, ElementRef, NgZone } from '@angular/core';

interface LogEventDTO {
    
    threadName: string;
    timestamp: number;

    logger: string;
    severity: string;

    message: string;

    exception: string;

    messageTemplate: string;
    argumentArray: any[];
    // msgParams: {[string],any};

    traceId: string;
    userName: string;
    traceRequest: string;
}


@Component({
  selector: 'app-serverlog',
  templateUrl: './serverlog.component.html'
})
export class AppServerLogComponent implements OnInit, OnDestroy {

  private serverUrl = 'http://localhost:8080/api/recentlog/sse/all'
//  private serverUrl = 'http://localhost:4200/api/recentlog/sse/all'

  @ViewChild('logTextArea') private textareaElement: ElementRef;

  @Input()
  traceId: string = null;

  @Input()
  username: string = null;

  @Input()
  fromTimestamp: number = null;

  @Input()
  toTimestamp: number = null;

  
  
  private source: EventSource;
    

  logEvents: LogEventDTO[]= [];
    resultText: string = '';
    
    statusMsg = '';

  constructor(private ngZone: NgZone) {
  }

  ngOnInit() {
    this.source = new EventSource(this.serverUrl); // TODO add "?traceId=..."

    this.source.addEventListener('log', sseEvent => {
      // console.log('onlog', sseEvent);
      this.ngZone.run(() => {
          this.onLogEventMessage(sseEvent);
      });
    }, false);

    this.source.onmessage = sseEvent => {
      console.log('onmessage', sseEvent);
    };

    this.source.addEventListener('open', e => {
      console.log("Connected");
      this.ngZone.run(() => {
        this.statusMsg = 'connected';
      });
    }, false);

    this.source.addEventListener('error', e => {
      console.log("connection error", e);
      this.ngZone.run(() => {
        this.statusMsg = 'error';
      });
    }, false);

  }

  ngOnDestroy() {
      if (this.source) {
          console.log("ngOnDestroy -> EventSource.close");
          try {
              this.source.close();
          } catch(err) {
              // ignore, should not occur
          }
          this.source = null;
      }
  }
  
  onLogEventMessage(sseEvent) {
        let logEvent = <LogEventDTO> JSON.parse(sseEvent.data);
        if (this.traceId != null && this.traceId !== logEvent.traceId) {
            return;
        }
        // console.log('onLogEventMessage', logEvent);
        this.logEvents.push(logEvent);
        this.resultText += 
            logEvent.message 
            + '\n';
        this.scrollToBottomTextArea();
  }

    onClickClear() {
        this.logEvents = [];
        this.resultText = '';
    }
    
    scrollToBottomTextArea() {
        if (this.textareaElement) {
            try {
                let elt = this.textareaElement.nativeElement;
                elt.scrollTop = elt.scrollHeight;
            } catch(err) {
                console.log('Failed scrollToBottomTextArea .. ignore', err);
            }
        } else {
            console.log('textArea element not found?.. ignore scroll');
        }
    }
    
}
