package fr.an.testdrools;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.testdrools.model.WebApp;
import fr.an.testdrools.model.WebAppDeployed;
import fr.an.testdrools.model.WebServer;

public class TestDroolsMain {
	
	private static final Logger LOG = LoggerFactory.getLogger(TestDroolsMain.class);
	
	public static void main(String[] args) {
		TestDroolsMain app = new TestDroolsMain();
		app.run();
	}
	
	public void run() {
		URL kmoduleURL = getClass().getClassLoader().getResource("META-INF/kmodule.xml");
		LOG.info("default kmodule:" + kmoduleURL);
		
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kc.newKieSession("Test1KS");

        // List<WebAppDeployed> deployedList = new ArrayList<>();
        // ksession.setGlobal("deployedList", deployedList);
        
        LOG.info("insert object facts");
        WebApp webApp1 = new WebApp("webApp1", "webApp1"); 
        WebServer webServer1 = new WebServer("webServer1", "webApp1"); 
        WebServer webServer2 = new WebServer("webServer2", "webApp1");
        
        FactHandle webApp1Handle = ksession.insert(webApp1);
        FactHandle webServer1Handle = ksession.insert(webServer1);
        FactHandle webServer2Handle = ksession.insert(webServer2);
        
        LOG.info("fireAllRules");
        ksession.fireAllRules();

        Collection<FactHandle> facts1 = new ArrayList<>(ksession.getFactHandles());
        Collection<?> objects1 = new ArrayList<>(ksession.getObjects());
        LOG.info("facts:" + facts1.size() + " objects:" + objects1.size());
        
        LOG.info("delete webServer2");
        ksession.delete(webServer2Handle);
        
        LOG.info("fireAllRules");
        ksession.fireAllRules();

        Collection<FactHandle> facts2 = new ArrayList<>(ksession.getFactHandles());
        Collection<?> objects2 = new ArrayList<>(ksession.getObjects());
        LOG.info("facts:" + facts2.size() + " objects:" + objects2.size());
        
        Collection<FactHandle> removedFacts = new ArrayList<>(facts1);
        removedFacts.removeAll(facts2);
        // removedFacts.remove(webServer2Handle);
        
        for(FactHandle f : removedFacts) {
        	System.out.println("removed fact: " + f.toExternalForm());
        	Object removedFactObject = ksession.getObject(f);
        	if (removedFactObject != null) {
        		System.out.println("removed fact.object: " + removedFactObject);
        	}
        }

        Collection<?> removedObjects = new ArrayList<>(objects1);
        removedObjects.removeAll(objects2);
        removedObjects.remove(webServer2);
        
        for(Object e : removedObjects) {
        	System.out.println("removed object: " + e);
        }

        LOG.info("finished");
        ksession.dispose();
	}
}
