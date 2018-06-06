package fr.an.tests.testxmpprocks;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.session.ConnectionEvent.Type;
import rocks.xmpp.core.session.TcpConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.session.XmppSessionConfiguration;
import rocks.xmpp.core.session.debug.ConsoleDebugger;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stanza.model.Presence;

public class App {

	/**
	 * <PRE>
	 * cd lets-chat$ 
	 * npm start
	 * </PRE>
	 * @throws Exception 
	 * 
	 */
	public static void main(String[] args) throws Exception {
		TcpConnectionConfiguration tcpConfiguration = TcpConnectionConfiguration.builder()
			    .hostname("localhost")
			    .port(5222)
			    .build();

//		BoshConnectionConfiguration boshConfiguration = BoshConnectionConfiguration.builder()
//		    .hostname("home")
//		    .port(5280)
//		    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("hostname", 3128)))
//		    .path("/http-bind/")
//		    .build();
//
//		WebSocketConnectionConfiguration webSocketConfiguration = WebSocketConnectionConfiguration.builder()
//		    .hostname("localhost")
//		    .port(5000)
//		    // .path("/ws/")
//		    // .sslContext(sslContext).secure(true)
//		    .build();

		
		XmppSessionConfiguration configuration = XmppSessionConfiguration.builder()
			    .debugger(ConsoleDebugger.class)
			    .build();
		XmppClient xmppClient = XmppClient.create("conference.home", configuration, tcpConfiguration
//				, boshConfiguration
//				, webSocketConfiguration
				);

		
		xmppClient.addConnectionListener(evt -> {
			Type type = evt.getType();
			System.out.println("ConnectionEvent " + type + " " + evt);
		});
		

		xmppClient.connect();
		System.out.println("connected");

		xmppClient.login("user1", "letschatuser1", "user1");
		System.out.println("logger-in");
		String user1AuthToken = "NTdjYzg0MTQyZTczZDFmMTBmOTA1YzVhOmY3MTZiNjM0NTY4YTVmZmRlYWRjN2Q0MjdhYTg5ZDI5OTRiOTY0MTBmYzhmODRkMQ==";

		xmppClient.connect(Jid.of("user1"));
		System.out.println("connect as user1");

		xmppClient.send(new Presence(Presence.Show.CHAT));
		
		xmppClient.addInboundMessageListener(msgEvt -> {
			// Object source = msgEvt.getSource();
			Message msg = msgEvt.getMessage();
			System.out.println("InboundMessage " + msg);
		});
		xmppClient.addInboundPresenceListener(e -> {
			System.out.println("InboundPresence " + e + " : " + e.getPresence());
		});
		
		xmppClient.addOutboundMessageListener(msgEvt -> {
			Message msg = msgEvt.getMessage();
			System.out.println("OutboundMessage " + msg);
		});
		xmppClient.addSendFailedListener((e,ex) -> {
			System.err.println("Failed send: " + e + " " + ex);
		});
		
		
		
		for (int i = 0; i < 3; i++) {
			xmppClient.send(new Message(Jid.of("arnaud"), Message.Type.CHAT, "Hi " + i));
			Thread.sleep(1000);
		}
		
		
		System.in.read();
		
		xmppClient.close();
	}
}
