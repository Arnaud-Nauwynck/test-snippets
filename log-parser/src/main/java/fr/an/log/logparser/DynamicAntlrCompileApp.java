package fr.an.log.logparser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.antlr.v4.Tool;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.apache.commons.io.FileUtils;

public class DynamicAntlrCompileApp {

    @SuppressWarnings("restriction")
	public static void main(String[] args) throws Exception {
    	String logGrammarFilename = "grammar-log.g"; // args[0]
        final String grammarName = "T";
        final String entryPoint = "parse";
    	
        // The grammar which echos the parsed characters to theconsole,
        // skipping any white space chars.
        final String grammar = FileUtils.readFileToString(new File(logGrammarFilename));
        

        // 1 - Write the `.g` grammar file to disk.
        Writer out = new BufferedWriter(new FileWriter(new File(grammarName + ".g")));
        out.write(grammar);
        out.close();

        // 2 - Generate the lexer and parser.
        Tool tool = new Tool(new String[]{grammarName + ".g"});
        // tool.process();
        tool.processGrammarsOnCommandLine();
        

        // 3 - Compile the lexer and parser.
        @SuppressWarnings("restriction")
		javax.tools.JavaCompiler compiler = javax.tools.ToolProvider.getSystemJavaCompiler();
        compiler.run(null, System.out, System.err, "-sourcepath", "", "-d", "target/classes", 
        		grammarName + "Parser.java"
        		, grammarName + "Lexer.java"
        		, grammarName + "Listener.java"
        		, grammarName + "BaseListener.java");

        // 4 - Parse the command line parameter using the dynamically created lexer and 
        //     parser with a bit of reflection Voodoo :)
        Class<Lexer> lexerClass = (Class<Lexer>) Class.forName(grammarName + "Lexer");
        Constructor<Lexer> lexerCtor = lexerClass.getConstructor(org.antlr.v4.runtime.CharStream.class);

        Class<Parser> parserClass = (Class<Parser>) Class.forName(grammarName + "Parser");
        Constructor<Parser> parserCTor = parserClass.getConstructor(org.antlr.v4.runtime.TokenStream.class);

        
        BufferedReader stdinReader = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        for(;;) {
        	System.out.println("enter line> ");
        	line = stdinReader.readLine();
        	if (line == null) {
        		break;
        	}
        	
        	line = line + "\n";
        	CharStream lineStream = new ANTLRInputStream(line);
        	Lexer lexer = (Lexer) lexerCtor.newInstance(lineStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            
            // introspection code for "new TParser(tokens)"
            Parser parser = (Parser)parserCTor.newInstance(tokens);
            
//        	if ( diag ) parser.addErrorListener(new DiagnosticErrorListener());
//        	if ( bail ) parser.setErrorHandler(new BailErrorStrategy());
//        	if ( SLL ) parser.getInterpreter().setPredictionMode(PredictionMode.SLL);


            // introspection code for "parser.grammar()"
            Method entryPointMethod = parserClass.getMethod(entryPoint);
            entryPointMethod.invoke(parser);

            
            // lexer.reset();
            
        }
        
    }
    
}