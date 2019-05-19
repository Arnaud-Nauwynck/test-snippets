import { Component, OnInit } from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

interface MessageDTO {
    strValue: string;
    intValue: number;
    args: string[];
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

  private serverUrl = 'http://localhost:8080/socket'
  private title = 'WebSockets chat';
  
  private ws: SockJS;
  private stompClient;
  
    messages: MessageDTO[]= [];
    
    sendText: string = '';
    resultText: string = '';
    
  constructor(){
    this.ws = new SockJS(this.serverUrl);
    this.stompClient = Stomp.over(this.ws);
  }
  
  ngOnInit() {
    this.connectStomp();
  }

  connectStomp() {
    this.stompClient.connect({}, (frame) => this.onStompConnect(frame));
  }

  disconnectStomp() {
    if (this.ws != null) {
      this.ws.ws.close();
    }
    this.setConnected(false);
    console.log("Disconnected");
  }
  
  onStompConnect(frame) {
      this.stompClient.subscribe("/chat", m => this.onStompMessage(m));
      this.stompClient.subscribe("/chatText", m => this.onStompMessageText(m));
  }

    onStompMessage(stompMessage) {
        console.log('onStompMessage', stompMessage)
        let body = JSON.parse(stompMessage.body);
        if(body) {
          console.log('onStompMessage', body);
          this.messages.push(body);
          this.resultText += body.strValue + ', intValue=' + body.intValue + ', args=' + body.args[0] + '..' + '\n';
        }
    }

    onStompMessageText(stompMessage) {
        console.log('onStompMessageText', stompMessage)
        let body = stompMessage.body;
        if(body) {
          console.log('onStompMessage', body);
          this.messages.push(body);
          this.resultText += body + '\n';
        }
    }
  
  onClickSend(){
    let msg: MessageDTO = { strValue: this.sendText, intValue: 123, args: ['hello', 'world'] };
    let sendMsgAsText = JSON.stringify(msg);
    this.stompClient.send("/app/send/message", {}, sendMsgAsText);
  }

  onClickSendText(){
    this.stompClient.send("/app/send/text", {}, this.sendText);
  }

}
