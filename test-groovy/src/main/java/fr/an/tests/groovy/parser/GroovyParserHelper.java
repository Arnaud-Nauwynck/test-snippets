package fr.an.tests.groovy.parser;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.UnicodeEscapingReader;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CompileUnit;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.collections.AST;

public class GroovyParserHelper {
    
    private static final Logger LOG = LoggerFactory.getLogger(GroovyParserHelper.class);
    
    public List<ASTNode> buildAstFromSource(String source) {
        AstBuilder astBuilder = new AstBuilder();
        List<ASTNode> astNodes = astBuilder.buildFromString(CompilePhase.CONVERSION, false, source);
        return astNodes;
    }

    public ClassNode buildAstClassNodeFromSource(String source) {
        AstBuilder astBuilder = new AstBuilder();
        List<ASTNode> astNodes = astBuilder.buildFromString(CompilePhase.CONVERSION, false, source);
        ClassNode res = (ClassNode) astNodes.stream().filter(x -> x instanceof ClassNode).findFirst().get();
        return res;
    }

    public AST parseGroovy(String src) throws RecognitionException, TokenStreamException {
        try {
            SourceBuffer sourceBuffer = new SourceBuffer();
            GroovyRecognizer parser = getGroovyParser(src, sourceBuffer);
    
            parser.compilationUnit();
    
            AST ast = parser.getAST();
    
            return ast;
        } catch(Exception ex) {
            LOG.error("Failed to parse", ex);
            throw new RuntimeException("Failed to parse", ex);
        }
    }

    private GroovyRecognizer getGroovyParser(String input, SourceBuffer sourceBuffer) {
        UnicodeEscapingReader unicodeReader = new UnicodeEscapingReader(new StringReader(input), sourceBuffer);
        GroovyLexer lexer = new GroovyLexer(unicodeReader);
        unicodeReader.setLexer(lexer);
        GroovyRecognizer parser = GroovyRecognizer.make(lexer);
        parser.setSourceBuffer(sourceBuffer);
        return parser;
    }
    
    public List<ClassNode> compilationUnit(String source, String scriptName) {
        GroovyCodeSource codeSource = new GroovyCodeSource(source, scriptName, "/groovy/script");
        GroovyClassLoader classLoader = new GroovyClassLoader();
        CompilationUnit cu = new CompilationUnit(); // CompilerConfiguration.DEFAULT, codeSource, classLoader);
        // cu.addPhaseOperation(, compilePhase);
        cu.addSource(codeSource.getName(), source);
        cu.compile();
        CompileUnit res = cu.getAST();
//        List<ModuleNode> resModules = res.getModules();
        List<ClassNode> resClasses = res.getClasses();
        return resClasses;
    }

    public String toJavaifierString(List<ClassNode> nodes) {
        StringWriter sw = new StringWriter(); 
        JavaifierGroovyPrettyPrinterVisitor visitor = new JavaifierGroovyPrettyPrinterVisitor(sw);
        for(ClassNode node : nodes) {
            // node.visit(visitor); ==> not implemented... throws ex !!
            visitor.visitClass(node);
        }
        return sw.toString();
    }
    
    
}
