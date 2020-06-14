package fr.an.tests.testglowroot.app2.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/api/app2"
        //, consumes = "application/json", produces = "application/json"
        )
@Slf4j
public class App2RestController {

    @AllArgsConstructor
    public static class FooResponseDTO {
        public String msg;
    }
    
    @GetMapping("/foo")
    public FooResponseDTO foo() {
        log.info("Rest /foo ..");

        FooResponseDTO res = new FooResponseDTO("test app2");
        log.info(".. done Rest /foo");
        return res;
    }
}
