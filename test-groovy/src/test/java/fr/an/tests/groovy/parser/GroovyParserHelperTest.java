package fr.an.tests.groovy.parser;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovyjarjarantlr.collections.AST;

public class GroovyParserHelperTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(GroovyParserHelperTest.class);
    
    @Test
    public void testParseGroovy() throws Exception {
        GroovyParserHelper sut = new GroovyParserHelper();
        String groovyText = FileUtils.readFileToString(new File("src/test/input1.groovy"));
        AST ast = sut.parseGroovy(groovyText);
        LOG.info("parse groovy => ast:" + ast);
    }
    
    @Test
    public void testBuildAstFromSource() throws Exception {
        GroovyParserHelper sut = new GroovyParserHelper();
        String groovyText = FileUtils.readFileToString(new File("src/test/input1.groovy"));
        List<ASTNode> ls = sut.buildAstFromSource(groovyText);
        LOG.info("buildAst => " + ls);
        ASTNode ast = ls.get(1);
//        GroovyCodeVisitor visitor = new GroovyCodeVisitor() {
//            
//        };
//        ast.visit(visitor );
    }
    
    @Test
    public void testCompile() throws Exception {
        GroovyParserHelper sut = new GroovyParserHelper();
        String groovyText = FileUtils.readFileToString(new File("src/test/input1.groovy"));
        List<ClassNode> res = sut.compilationUnit(groovyText, "input1.groovy");
        LOG.info("compile => " + res);
    }
    
    @Test
    public void testToJavaifierString() throws Exception {
        GroovyParserHelper sut = new GroovyParserHelper();
        String groovyText = FileUtils.readFileToString(new File("src/test/input1.groovy"));
        // List<ASTNode> ls = sut.buildAstFromSource(groovyText);
        List<ClassNode> ls = sut.compilationUnit(groovyText, "input1.groovy");
        String res = sut.toJavaifierString(ls);
        LOG.info("groovy=>pseudo java: " + res);
    }
}
