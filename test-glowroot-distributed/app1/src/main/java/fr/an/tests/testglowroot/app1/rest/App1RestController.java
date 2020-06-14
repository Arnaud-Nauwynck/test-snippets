package fr.an.tests.testglowroot.app1.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.an.tests.testglowroot.app1.service.App1Service;
import fr.an.tests.testglowroot.app1.service.App2RestCli;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/api/app1"
        //, consumes = "application/json", produces = "application/json"
        )
@Slf4j
public class App1RestController {

    @Autowired
    private App2RestCli app2RestCli;

    @Autowired
    private App1Service delegate;

    @AllArgsConstructor
    public static class FooResponseDTO {
        public String msg;
    }
    
    @GetMapping("/foo")
    public FooResponseDTO foo() {
        log.info("Rest /foo ..");

        FooResponseDTO res = new FooResponseDTO("test");
        log.info(".. done Rest /foo");
        return res;
    }
    
    @GetMapping("/foo-app2")
    public FooResponseDTO fooApp2() {
        log.info("Rest /foo-app2 ..");

        val tmpres = app2RestCli.foo();
        
        FooResponseDTO res = new FooResponseDTO(tmpres.msg);
        log.info(".. done Rest /foo-app2");
        return res;
    }
    
    @GetMapping("/f")
    public void f() {
        delegate.f();
    }

    @GetMapping("/g")
    public void g() {
        delegate.g();
    }

}
