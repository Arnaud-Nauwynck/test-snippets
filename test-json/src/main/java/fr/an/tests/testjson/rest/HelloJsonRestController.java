package fr.an.tests.testjson.rest;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;

@RestController
@RequestMapping("/springweb/hello")
public class HelloJsonRestController {

    @RequestMapping("/helloString")
    @ResponseBody
    public String home() {
        return "Hello World!";
    }

    @RequestMapping("/helloArray")
    @ResponseBody
    public String[] homeArray() {
        return new String[] { "Hello", "World!" };
    }

    @RequestMapping("/helloMap")
    @ResponseBody
    public Map<String,String> homeMap() {
        return ImmutableMap.of("en", "Hello World");
    }

    @RequestMapping("/helloObj")
    @ResponseBody
    public Salutation homeObj() {
        return new Salutation("Hello");
    }

}
