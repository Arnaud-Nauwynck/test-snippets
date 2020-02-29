package fr.an.test.testpostgresqlhib;

import java.util.concurrent.Callable;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class XAService {

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void xanew(Runnable action) {
        action.run();
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public <T> T xanew(Callable<T> action) {
        try {
            return action.call();
        } catch(Exception ex) {
            throw new RuntimeException("Failed", ex);
        }
    }

}
