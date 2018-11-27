package fr.an.tests.springboottx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 
 */
@SpringBootApplication
public class SpringTxApp {

	public static void main(String[] args) {
		SpringApplication.run(SpringTxApp.class, args);
	}

}

@Component
class AppCommandRunner implements CommandLineRunner {
	
	private static final Logger log = LoggerFactory.getLogger(AppCommandRunner.class);

	@Autowired private TxService txService;
	
	@Override
	public void run(String... args) throws Exception {
		log.info("(1) run @Transactional foo() ..");
		txService.foo();

		log.info("(2) run @Transactional foo() ..");
		txService.foo();

		log.info("(3) run @Transactional fooEx()..");
		try {
			txService.fooEx();
		} catch(Exception ex) {
			log.info("ok got exception, rolled-backed.." + ex.getMessage());
		}

		log.info("(4) run @Transactional foo() ..");
		txService.foo();
	}
	
}

class TxResource {
	private int id;

	public TxResource(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "TxResource(" + id + ")";
	}
}

class UnboundedTxResource {
	private int id;

	public UnboundedTxResource(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "UnboundedTxResource(" + id + ")";
	}
}

@Component
@Transactional
class TxService {
	
	private static final Logger log = LoggerFactory.getLogger(TxService.class);

	private static final String TX_RESOURCE_KEY = "txResourceKey";
	private static final String TX_UNBOUNDEDRESOURCE_KEY2 = "txResourceKey2";
	private int idTxResourceGenerator = 0;
	private int idUnboundedTxResourceGenerator = 0;
	
	public void foo() {
		registerSynchronisationListener("synchronisationListener1");
		registerSynchronisationListener("synchronisationListener2");
		
		TxResource txResource = gerOrRegisterTxResource();
		TxResource txResourceCheck = gerOrRegisterTxResource();
		if (txResourceCheck != txResource) {
			throw new IllegalStateException("should not occurs");
		}
		
		UnboundedTxResource ur = gerOrRegisterUnboundedTxResource2();
		UnboundedTxResource urCheck = gerOrRegisterUnboundedTxResource2();
		if (ur != urCheck) {
			throw new IllegalStateException("should not occurs");
		}
	}

	public void fooEx() {
		registerSynchronisationListener("synchronisationListener1");
		registerSynchronisationListener("synchronisationListener2");

		TxResource txResource = gerOrRegisterTxResource();
		TxResource txResourceCheck = gerOrRegisterTxResource();
		if (txResourceCheck != txResource) {
			throw new IllegalStateException("should not occurs");
		}

		UnboundedTxResource ur = gerOrRegisterUnboundedTxResource2();
		UnboundedTxResource urCheck = gerOrRegisterUnboundedTxResource2();
		if (ur != urCheck) {
			throw new IllegalStateException("should not occurs");
		}

		throw new RuntimeException("rolling back..");
	}

	private TxResource gerOrRegisterTxResource() {
		TxResource res = (TxResource) TransactionSynchronizationManager.getResource(TX_RESOURCE_KEY);
		if (null == res) {
			int id = ++idTxResourceGenerator;
			res = new TxResource(id);
			log.info("creating TxResource " + id);
			registerBindCleanupSynchronisationListener("bindCleanup for txResource", TX_RESOURCE_KEY, res);
		}
		return res;
	}

	private UnboundedTxResource gerOrRegisterUnboundedTxResource2() {
		UnboundedTxResource res = (UnboundedTxResource) TransactionSynchronizationManager.getResource(TX_UNBOUNDEDRESOURCE_KEY2);
		if (null == res) {
			int id = ++idUnboundedTxResourceGenerator;
			res = new UnboundedTxResource(id);
			log.info("creating UnboundedTxResource " + id);
			TransactionSynchronizationManager.bindResource(TX_UNBOUNDEDRESOURCE_KEY2, res);
		}
		return res;
	}

	private void registerSynchronisationListener(String displayName) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

			@Override
			public void resume() {
				log.info(displayName + " Tx resume..");
			}

			@Override
			public void flush() {
				log.info(displayName + " Tx flush..");
			}

			@Override
			public void beforeCommit(boolean readOnly) {
				log.info(displayName + " Tx beforeCommit("  + readOnly + ")..");
			}

			@Override
			public void beforeCompletion() {
				log.info(displayName + " Tx beforeCompletion..");
			}

			@Override
			public void afterCommit() {
				log.info(displayName + " Tx afterCommit..");
			}

			@Override
			public void afterCompletion(int status) {
				log.info(displayName + " Tx afterCompletion(" + status + ")");
			}
			
		});
	}


	private void registerBindCleanupSynchronisationListener(String displayName, Object resourceKey, Object resourceValue) {
		TransactionSynchronizationManager.bindResource(resourceKey, resourceValue);
		
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

			@Override
			public void afterCompletion(int status) {
				log.info(displayName + " Tx afterCompletion(" + status + ") => unbindResource");
				TransactionSynchronizationManager.unbindResource(resourceKey);
			}

			@Override
			public void suspend() {
				log.info(displayName + " Tx suspend => unbindResource");
				TransactionSynchronizationManager.unbindResource(resourceKey);
			}

			@Override
			public void resume() {
				log.info(displayName + " Tx resume => bindResource");
				TransactionSynchronizationManager.bindResource(resourceKey, resourceValue);
			}
		});
	}

}

