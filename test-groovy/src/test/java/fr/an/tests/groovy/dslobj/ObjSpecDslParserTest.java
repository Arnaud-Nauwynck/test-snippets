package fr.an.tests.groovy.dslobj;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjSpecDslParserTest {

    
    private static final Logger LOG = LoggerFactory.getLogger(ObjSpecDslParserTest.class);
    
    @Test
    public void testParse() {
        // Prepare
        LOG.info("@Test testParse");
        ObjSpecDslParser sut = new ObjSpecDslParser();
        String scriptText = "a {\n" 
            + "b = 1\n"
            + "}";
        // Perform
        Object res = sut.parse(scriptText);
        // Post-check
        Assert.assertNotNull(res);
    }

    @Test
    public void testParse_2() {
        // Prepare
        LOG.info("@Test testParse_2");
        ObjSpecDslParser sut = new ObjSpecDslParser();
        String scriptText = "a 'paramA1' 'paramA2' paramA3 {\n" 
            + "b = 1+1\n"
            + "}";
        // Perform
        Object res = sut.parse(scriptText);
        // Post-check
        Assert.assertNotNull(res);
    }

    @Test
    public void testParse_ls() {
        // Prepare
        LOG.info("@Test testParse_ls");
        ObjSpecDslParser sut = new ObjSpecDslParser();
        String scriptText = "a 'paramA1', 'paramA2', 'paramA3', 'paramA4', paramA5 {\n" 
            + "b = 1+1\n"
            + "}";
        // Perform
        Object res = sut.parse(scriptText);
        // Post-check
        Assert.assertNotNull(res);
    }

    @Test
    public void testParse_obj() {
        // Prepare
        LOG.info("@Test testParse_obj");
        ObjSpecDslParser sut = new ObjSpecDslParser();
        String scriptText = "obj 'a' {\n" 
            + "b = 2\n"
            + "}";
        // Perform
        Object res = sut.parse(scriptText);
        // Post-check
        Assert.assertNotNull(res);
    }

    @Test
    public void testParse_obj2() {
        // Prepare
        LOG.info("@Test testParse_obj2");
        ObjSpecDslParser sut = new ObjSpecDslParser();
        String scriptText = "obj 'a' 'test' {\n" 
            + "b = 2\n"
            + "}";
        // Perform
        Object res = sut.parse(scriptText);
        // Post-check
        Assert.assertNotNull(res);
    }

    @Test
    public void testParse_objLs() {
        // Prepare
        LOG.info("@Test testParse_objLs");
        ObjSpecDslParser sut = new ObjSpecDslParser();
        String scriptText = "obj 'a', 'paramA1', 'paramA2', 'paramA3', 'test' {\n" 
            + "b = 2\n"
            + "}";
        // Perform
        Object res = sut.parse(scriptText);
        // Post-check
        Assert.assertNotNull(res);
    }

    @Test
    public void testParse_objLsComma() {
        // Prepare
        LOG.info("@Test testParse_objLs");
        ObjSpecDslParser sut = new ObjSpecDslParser();
        String scriptText = "obj 'a', 'paramA1', 'paramA2', 'paramA3', 'test', {\n" 
            + "b = 2\n"
            + "}";
        // Perform
        Object res = sut.parse(scriptText);
        // Post-check
        Assert.assertNotNull(res);
    }

    @Test
    public void testParse_objLsMeth() {
        // Prepare
        LOG.info("@Test testParse_objLs");
        ObjSpecDslParser sut = new ObjSpecDslParser();
        String scriptText = "obj 'a', 'paramA1', 'paramA2', 'paramA3', test {\n" 
            + "b = 2\n"
            + "}";
        // Perform
        Object res = sut.parse(scriptText);
        // Post-check
        Assert.assertNotNull(res);
    }

}
