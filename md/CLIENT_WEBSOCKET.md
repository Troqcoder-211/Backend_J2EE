### TEST SOCKET 
```bash
WebSocket URL:
  ws://localhost:8080/ws
```

### SECURITY
```bash
@Override
public void configureClientInboundChannel(ChannelRegistration registration) {
  registration.interceptors(new AuthChannelInterceptorAdapter(jwtUtils));}
```

### FRONTEND
```bash
npm install stompjs sockjs-client

import SockJS from 'sockjs-client';
import { over } from 'stompjs';

let stompClient = null;

export const connectWebSocket = (userId, conversationId, onMessage, onNotification) => {
  const socket = new SockJS("http://localhost:8080/ws");
  stompClient = over(socket);

  stompClient.connect({}, () => {
    console.log("✅ Connected to WebSocket");

    // Sub message channel
    stompClient.subscribe(`/topic/conversations/${conversationId}`, (msg) => {
      onMessage(JSON.parse(msg.body));
    });

    // Sub notification channel
    stompClient.subscribe(`/queue/notifications/${userId}`, (msg) => {
      onNotification(JSON.parse(msg.body));
    });
  });
};

export const sendMessage = (msg) => {
  stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(msg));
};

export const sendNotification = (noti) => {
  stompClient.send("/app/notifications.send", {}, JSON.stringify(noti));
};

```


