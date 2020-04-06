package fr.an.test.testpostgresqlhib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.val;

@SpringBootTest
class TestPostgresqlJpaApplicationTests {

    @Autowired
    private XAService xa;

    @Autowired
    private EntityManager em;

    @Autowired
    private ExecTaskService execTaskService;

    @Autowired
    private ExecTaskRepository execTaskRepo;

    // @Test
    public void testInsertData() {
        if (execTaskRepo.countByStatus(ExecTaskStatus.TO_EXECUTE) < 100_000) {
            xa.xanew(() -> execTaskService.insertData(100_000));
        }
    }

    private static class ThreadResult {
        List<Long> ids = new ArrayList<>();
    }

    /**
     * 
        Thread[0] 91 
        Thread[1] 89 
        Thread[2] 89 
        Thread[3] 90 
        Thread[4] 93 
        total: 452
     */
    @Test
    public void testParallel_jdbc() throws InterruptedException {
        int threadCount = 5;
        Object lock = new Object();
        AtomicInteger threadStoppedCount = new AtomicInteger();
        AtomicBoolean shouldStop = new AtomicBoolean(false);
        ThreadResult[] threadResults = new ThreadResult[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threadResults[i] = new ThreadResult();
        }
        for (int i = 0; i < threadCount; i++) {
            final ThreadResult threadResult = threadResults[i];
            new Thread(() -> {
                for (;;) {
                    xa.xanew(() -> {
                        long taskId = execTaskService.pollLock1TaskToExecute_jdbc();
                        if (taskId > 0) {
                            val task = execTaskRepo.getOne(taskId);
                            task.setStatus(ExecTaskStatus.RUNNING);
                            threadResult.ids.add(taskId);
                        }
                    });
                    if (shouldStop.get()) {
                        synchronized (lock) {
                            threadStoppedCount.incrementAndGet();
                        }
                        break;
                    }
                }
            }).start();
        }
        Thread.sleep(1_000);
        shouldStop.set(true);
        while (threadStoppedCount.get() != threadCount) {
            Thread.sleep(100);
        }

        xa.xanew(() -> {
            int count = 0;
            Set<Long> foundIds = new HashSet<>();
            for (int i = 0; i < threadCount; i++) {
                List<Long> ids = threadResults[i].ids;
                System.out.println("Thread[" + i + "] " + ids.size() + " ");
                for (val id : ids) {
                    count++;
                    val added = foundIds.add(id);
                    if (!added) {
                        System.err.println("FAILED id already polled: " + id);
                    }
                    execTaskRepo.deleteById(id);
                }
            }
            System.out.println("total: " + count);
        });
    }

    /**
Thread[0] 83 
Thread[1] 82 
Thread[2] 81 
Thread[3] 80 
Thread[4] 83 
total: 409
     */
    @Test
    public void testParallel_jpa() throws InterruptedException {
        int threadCount = 5;
        Object lock = new Object();
        AtomicInteger threadStoppedCount = new AtomicInteger();
        AtomicBoolean shouldStop = new AtomicBoolean(false);
        ThreadResult[] threadResults = new ThreadResult[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threadResults[i] = new ThreadResult();
        }
        for (int i = 0; i < threadCount; i++) {
            final ThreadResult threadResult = threadResults[i];
            new Thread(() -> {
                for (;;) {
                    xa.xanew(() -> {
                        long taskId = execTaskService.pollLock1TaskToExecute_jpa();

                        if (taskId > 0) {
                            val task = execTaskRepo.getOne(taskId);
                            task.setStatus(ExecTaskStatus.RUNNING);
                            threadResult.ids.add(taskId);
                        }
                    });
                    if (shouldStop.get()) {
                        synchronized (lock) {
                            threadStoppedCount.incrementAndGet();
                        }
                        break;
                    }
                }
            }).start();
        }
        Thread.sleep(1_000);
        shouldStop.set(true);
        while (threadStoppedCount.get() != threadCount) {
            Thread.sleep(100);
        }

        xa.xanew(() -> {
            int count = 0;
            Set<Long> foundIds = new HashSet<>();
            for (int i = 0; i < threadCount; i++) {
                List<Long> ids = threadResults[i].ids;
                System.out.println("Thread[" + i + "] " + ids.size() + " ");
                for (val id : ids) {
                    val added = foundIds.add(id);
                    if (!added) {
                        System.err.println("FAILED id already polled: " + id);
                    }
                    count++;
                    execTaskRepo.deleteById(id);
                }
            }
            System.out.println("total: " + count);
        });
    }

    /**
Thread[0] 156 
Thread[1] 153 
Thread[2] 154 
Thread[3] 154 
Thread[4] 156 
total: 773
     */
    @Test
    public void testParallel_jdbc_storedproc() throws InterruptedException {
        int threadCount = 5;
        Object lock = new Object();
        AtomicInteger threadStoppedCount = new AtomicInteger();
        AtomicBoolean shouldStop = new AtomicBoolean(false);
        ThreadResult[] threadResults = new ThreadResult[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threadResults[i] = new ThreadResult();
        }
        for (int i = 0; i < threadCount; i++) {
            final ThreadResult threadResult = threadResults[i];
            new Thread(() -> {
                for (;;) {
                    xa.xanew(() -> {
                        long taskId = execTaskService.pollLock1TaskToExecute_jdbc_storedproc();
                        if (taskId > 0) {
                            threadResult.ids.add(taskId);
                        }
                    });
                    if (shouldStop.get()) {
                        synchronized (lock) {
                            threadStoppedCount.incrementAndGet();
                        }
                        break;
                    }
                }
            }).start();
        }
        Thread.sleep(1_000);
        shouldStop.set(true);
        while (threadStoppedCount.get() != threadCount) {
            Thread.sleep(100);
        }

        xa.xanew(() -> {
            int count = 0;
            Set<Long> foundIds = new HashSet<>();
            for (int i = 0; i < threadCount; i++) {
                List<Long> ids = threadResults[i].ids;
                System.out.println("Thread[" + i + "] " + ids.size() + " ");
                for (val id : ids) {
                    val added = foundIds.add(id);
                    if (!added) {
                        System.err.println("FAILED id already polled: " + id);
                    }
                    count++;
                    execTaskRepo.deleteById(id);
                }
            }
            System.out.println("total: " + count);
        });
    }

    /**
Thread[0] 153 
Thread[1] 156 
Thread[2] 153 
Thread[3] 156 
Thread[4] 157 
total: 775
     */
    @Test
    public void testParallel_jdbc_storedproc_delPrevTask() throws InterruptedException {
        int threadCount = 5;
        Object lock = new Object();
        AtomicInteger threadStoppedCount = new AtomicInteger();
        AtomicBoolean shouldStop = new AtomicBoolean(false);
        ThreadResult[] threadResults = new ThreadResult[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threadResults[i] = new ThreadResult();
        }
        for (int i = 0; i < threadCount; i++) {
            final ThreadResult threadResult = threadResults[i];
            new Thread(() -> {
                AtomicLong prevTaskId = new AtomicLong();
                for (;;) {
                    xa.xanew(() -> {
                        long taskId = execTaskService.pollLock1TaskToExecute_jdbc_storedproc_delPrevTask(prevTaskId.get());
                        prevTaskId.set(taskId);
                        if (taskId > 0) {
                            threadResult.ids.add(taskId);
                        }
                    });
                    if (shouldStop.get()) {
                        synchronized (lock) {
                            threadStoppedCount.incrementAndGet();
                        }
                        break;
                    }
                }
            }).start();
        }
        Thread.sleep(1_000);
        shouldStop.set(true);
        while (threadStoppedCount.get() != threadCount) {
            Thread.sleep(100);
        }

        xa.xanew(() -> {
            int count = 0;
            Set<Long> foundIds = new HashSet<>();
            for (int i = 0; i < threadCount; i++) {
                List<Long> ids = threadResults[i].ids;
                System.out.println("Thread[" + i + "] " + ids.size() + " ");
                for (val id : ids) {
                    val added = foundIds.add(id);
                    if (!added) {
                        System.err.println("FAILED id already polled: " + id);
                    }
                    count++;
                }
            }
            System.out.println("total: " + count);
        });
    }


    
    // ------------------------------------------------------------------------

    public void xaExecMeasureTime(String msg, int size, Runnable run) {
        xa.xanew(() -> {
            execMeasureTime(msg, size, run);
        });
    }

    public void xarollbackExecMeasureTime(String msg, int size, Runnable run) {
        try {
            xa.xanew(() -> {
                execMeasureTime(msg, size, run);
                // ?? em.flush(); // flush buffers but do not commit.. rollback
                throw new ForceRollbackRuntimeException();
            });
        } catch (ForceRollbackRuntimeException ex) {
            // ok, no rethrow
        }
    }

    protected static class ForceRollbackRuntimeException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    public void execMeasureTime(String msg, int size, Runnable run) {
        long start = System.nanoTime();

        run.run();

        long elapsed = System.nanoTime() - start;
        System.out.println(msg + "[" + size + "] took " + TimeUnit.NANOSECONDS.toMillis(elapsed) + "ms" + " "
                + String.format("%.3f", ((double) TimeUnit.NANOSECONDS.toMillis(elapsed) / size)) + " ms/u");
    }

}
