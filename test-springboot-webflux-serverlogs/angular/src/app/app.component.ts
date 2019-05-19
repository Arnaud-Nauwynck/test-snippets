
import { Component, OnInit, NgZone } from '@angular/core';

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
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

  private serverUrl = 'http://localhost:8080/api/recentlog/sse/all'
//  private serverUrl = 'http://localhost:4200/api/recentlog/sse/all'

    private source: EventSource;

    logEvents: LogEventDTO[]= [];
    resultText: string = '';

    statusMsg = '';

  constructor(private ngZone: NgZone) {
  }

  ngOnInit() {
    this.source = new EventSource(this.serverUrl);

    this.source.addEventListener('log', sseEvent => {
      console.log('onlog', sseEvent);
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
        console.log("Error", e);
      this.ngZone.run(() => {
        this.statusMsg = 'error';
      });
    }, false);

  }

    onLogEventMessage(sseEvent) {
        let logEvent = <LogEventDTO> JSON.parse(sseEvent.data);
        console.log('onLogEventMessage', logEvent);
        this.logEvents.push(logEvent);
        this.resultText += 
            logEvent.message 
            + '\n';
    }

}
