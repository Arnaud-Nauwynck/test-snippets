/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.activemq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

//	@JmsListener(destination = "TestQueue1")
//	public void receiveQueue1(String text) {
//		doLog("TestQueue1", text);
//	}
//
//	@JmsListener(destination = "TestTopic1", containerFactory="jmsListenerContainerTopic")
//	public void receiveTopic1_cont(String text) {
//		doLog("TestTopic1 using jmsListenerContainerTopic", text);
//	}
//
//	@JmsListener(destination = "TestQueue1", containerFactory="jmsListenerContainerQueue")
//	public void receiveQueue1_cont(String text) {
//		doLog("TestQueue1 using jmsListenerContainerQueue", text);
//	}
//	
//	// DOES NOT WORK!!
//	@JmsListener(destination = "TestTopic1")
//	public void receiveTopic1_err(String text) {
//		doLog("TestTopic1", text);
//	}

	// DOES NOT WORK!!
	@JmsListener(destination = "topic://TestTopic1")
	public void receiveTopic(String text) {
		doLog("topic://TestTopic1", text);
	}
	
	protected void doLog(String from, String msg) {
		System.out.println("\n **** " + from + " => " + msg + "\n");
	}
	
}
