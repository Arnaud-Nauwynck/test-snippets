package fr.an.test.testpostgresqljpa;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.junit.Assert;
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
    private FooService fooService;

    @Autowired
    private FooRepository fooRepo;

	@Test
	public void testInsertData() {
	    if (fooRepo.count() < 100_000) {
	        xa.xanew(() -> fooService.insertData(100_000));
	    }
	}

	@Test
	public void testFindByIds() {
	    int max = (int) fooRepo.count();

	    {
    	    int count = 10000;
            int[] ids = RandomUtils.randomIntArray(count, max);
            Arrays.sort(ids); // may contains duplicates..
            Set<Integer> idSet = new HashSet<>();
            for(val e : ids) {
                idSet.add(e);
            }
            
            xaExecMeasureTime("FindByIds", count, () -> {
                List<FooEntity> res = fooService.findByIds(ids);
                checkIds(idSet, res);
                System.out.println("test " + count + " found.. " + res.size());

                List<FooEntity> res2 = fooService.findByIds2(ids);
                checkIds(idSet, res2);
//                
//                List<FooEntity> res3 = fooService.findByIds_FUNCTION_ANY_arrayStr(ids);
//                checkIds(idSet, res3);
                
            });
	    }
	    
	    for (int count = 1; count < 10000; count *= 2) {
	        final int fcount = count;
	        int[] ids = RandomUtils.randomIntArray(count, max);
	        Arrays.sort(ids);
	        xaExecMeasureTime("FindByIds", fcount, () -> {
	            fooService.findByIds(ids);
	        });

//	        xaExecMeasureTime("findByIds_SQL_ANY_arrayStr", fcount, () -> {
//                fooService.findByIds_SQL_ANY_arrayStr(ids);
//            });
//            xaExecMeasureTime("findByIds_FUNCTION_ANY_arrayStr", fcount, () -> {
//                fooService.findByIds_FUNCTION_ANY_arrayStr(ids);
//            });

	        
	        xaExecMeasureTime("Loop-FindByIds", fcount, () -> {
                fooService.findByIds_loop(ids);
            });
	    }
	}

    private void checkIds(Set<Integer> idSet, List<FooEntity> res) {
        for(val e : res) {
            Assert.assertTrue(idSet.contains(e.getId()));
        }
    }

    @Test
    public void testInsert() {
        final int count = 5000;
        xarollbackExecMeasureTime("Insert_unnest", count, () -> {
            fooService.bulkInsert_unnest(count);
        });
    }
    
    @Test
    public void testInsert_commit() {
        final long prevCount = xa.xanew(() -> fooRepo.count());
        final int count = 5000;
        xaExecMeasureTime("Insert_unnest", count, () -> {
            fooService.bulkInsert_unnest(count);
        });
        final long newCount = xa.xanew(() -> fooRepo.count());
        Assert.assertEquals(count, newCount-prevCount);
    }
    
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
                throw new ForceRollbackRuntimeException ();
            });
       } catch(ForceRollbackRuntimeException ex) {
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
        System.out.println(msg + "[" + size + "] took " 
                + TimeUnit.NANOSECONDS.toMillis(elapsed) + "ms"
                + " " + String.format("%.3f", ((double)TimeUnit.NANOSECONDS.toMillis(elapsed)/size)) + " ms/u"); 
	}
	
}
