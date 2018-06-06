'use strict';
angular.module('myapp', [ ])
.controller('MyController', function($scope, $http) {
    var self = this;

    self.useImpl = "spring5";
    self.from = "me";
    self.chatRoom = "Default";
    self.msgToSend = "";
    self.formattedMessages = '';
    
    self.eventSource = null;
    self.lastEventId = 0;
    self.connStatus = '';
    
    self.onInit = function() {
        // $scope.on("destroy", function() { self.onDispose(); });
        self.connectEventSource();
    };
    
    self.onDispose = function() {
        console.log("onDispose");
        self.eventSource.close();
    };
    
    self.connectEventSource = function() {
        console.log("subscribe chatRoom: " + self.chatRoom + " useImpl:" + self.useImpl + " lastEventId:" + self.lastEventId);
        self.eventSource = new EventSource('/app/chat/room/' + self.chatRoom + '/subscribeMessagesSpring' + (self.useImpl === 'spring4'? '4' : '5'),
                { id: self.lastEventId });
        
        if (self.lastEventId > 0) {
            self.eventSource.id = self.lastEventId;
        }
        
        self.eventSource.addEventListener('open', function(e) {
            console.log("onopen", e);
            self.connStatus = 'connected';
            $scope.$apply();
        }, false);

        self.eventSource.addEventListener('error', function(e) {
            if (e.eventPhase == EventSource.CLOSED) {
              console.log('connection closed (..reconnect)', e);
              self.connStatus = 'connection closed (..auto reconnect in 3s)';
            } else {
              console.log("onerror", e);
              self.connStatus = 'error\n';
            }
            $scope.$apply();
          }, false);
        
        self.eventSource.addEventListener('message', function(e) {
            console.log("onmessage", e);
            self.handleMessageEvent(e);
            $scope.$apply();
        }, false);
        
        self.eventSource.addEventListener("chat", function(e) {
            // never called? .. cf onmessage !
            console.log("on event 'chat'", e);
            self.handleMessageEvent(e);
            $scope.$apply();
        });
    }

    self.handleMessageEvent = function(e) {
        var msg = JSON.parse(e.data);
        self.lastEventId = msg.id;
        self.eventSource.id = self.lastEventId;
        self.addFormattedEvent(msg.id, new Date(msg.date), msg.from, msg.msg);
    };
    
    self.onClickSendMsg = function () {
        console.log("send");
        var msg = self.msgToSend;
        // self.addFormattedEvent(new Date(), self.from, msg);
        
        var req = { onBehalfOf: self.from, msg };
        self.sending = true;
        $http.post("/app/chat/room/" + self.chatRoom, req)
        .then(function() {
          self.sending = false;
        }, function(err) {
          self.addFormattedEvent(-1, new Date(), self.from, "Failed to send " + msg + ":" + err);
          self.sending = false;
        });
    };
    
    self.addFormattedEvent = function(id, date, from, msg) {
       self.formattedMessages += "[" + id + "] " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + " " +  
           ((self.from === from)? "" : "(" + from + ") ") + 
           msg + "\n\n";
    }
    
    self.onClickReconnect = function() {
        self.formattedMessages += "** Click RECONNECT **";
        self.eventSource.close();
        self.connectEventSource();
    };
    
    self.onInit();
});
