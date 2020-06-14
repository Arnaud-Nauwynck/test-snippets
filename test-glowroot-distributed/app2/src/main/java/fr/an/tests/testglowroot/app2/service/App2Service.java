package fr.an.tests.testglowroot.app2.service;

import org.glowroot.agent.api.Instrumentation;
import org.springframework.stereotype.Service;

@Service
public class App2Service {


    @Instrumentation.Timer("f")
    public void f() {
        sleep(1000);
        f_b();
        
        sleep(1000);
    }

    public void f_b() {
        f2();
    }

    @Instrumentation.Timer("f2")
    public void f2() {
        sleep(100);
        f2_b();
        sleep(100);
    }

    public void f2_b() {
        sleep(100);
        f2_c();
    }

    public void f2_c() {
        sleep(1000);
    }

    @Instrumentation.TraceEntry(message = "process g", timer = "g")
    public void g() {
        sleep(1000);
        g_b();
        sleep(1000);
    }

    public void g_b() {
        g2();
    }

    @Instrumentation.TraceEntry(message = "process g2", timer = "g2")
    public void g2() {
        sleep(100);
        g2_b();
    }

    public void g2_b() {
        sleep(1000);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }
}
