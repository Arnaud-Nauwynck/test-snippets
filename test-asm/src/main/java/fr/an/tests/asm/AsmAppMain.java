package fr.an.tests.asm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.util.TraceSignatureVisitor;

import fr.an.tests.asm.utils.LsUtils;
import lombok.Getter;
import lombok.val;

public class AsmAppMain {

	private List<String> classpath = new ArrayList<String>();
	
	public static void main(String[] args) {
		try {
			new AsmAppMain().run(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed .. exiting");
		}
	}

	private void run(String[] args) throws Exception {
		for(int i = 0; i < args.length; i++) {
			val arg = args[i];
			if (arg.equalsIgnoreCase("-cp")) {
				val sep = System.getProperty("path.separator");
				val cp = args[++i].split(sep);
				classpath.addAll(Arrays.asList(cp));
			} else {
				throw new UnsupportedOperationException("unrecognized arg " + arg);
			}
		}
		
		classpath.add("C:/arn/downloadTools/spark/spark3/core/target/spark-core_2.12-3.4.0-SNAPSHOT.jar");
		
		// scan classpath
		for(val classpathElt : classpath) {
			System.out.println("scan " + classpathElt);
			File f = new File(classpathElt);
			if (f.isDirectory()) {
				// TODO recursive scan
			} else if (f.getName().endsWith(".jar")) {
				ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(f)));
				
				for(ZipEntry ze = zin.getNextEntry(); ze != null; ze = zin.getNextEntry()) {
					val entryName = ze.getName();
					if (entryName.endsWith(".class")) {
						System.out.println("scan .class Zip entry " + f + ": " + ze);
					
						val bytes = IOUtils.toByteArray(zin);
						scanClassBytes(entryName, bytes); 

					}
				}
			}
		}
	}

	private void scanClassBytes(String entryName, final byte[] bytes) {
		ClassReader cr = new ClassReader(bytes);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		
		Set<String> formalTypeParams = new HashSet<>();
		if (cn.signature != null) {
			val typeArgsSig = new TypeArgCollectorSignatureVisitor();
			new SignatureReader(cn.signature).accept(typeArgsSig);
			val typeArgs = typeArgsSig.getTypeArguments();
			System.out.println(" .. class sig " + typeArgs);
			formalTypeParams.addAll(typeArgsSig.getFormalTypeParams());

//			val dump = new DumpSignatureVisitor();
//			new SignatureReader(cn.signature).accept(dump);
//			System.out.println(" " + dump.getDumpText());

		}
		
		val fields = cn.fields;
		if (fields != null && fields.size() != 0) {
			for(val field: fields) {
				val acc = field.access;
				if (! Modifier.isStatic(acc)) {
					String typeDecl;
					if (field.signature == null) {
						val type = org.objectweb.asm.Type.getType(field.desc);
						typeDecl = type.getClassName();
					} else {
						val typeArgsSig = new TypeArgCollectorSignatureVisitor();
						new SignatureReader(field.signature).accept(typeArgsSig);
						
						val traceVis = new TraceSignatureVisitor(0);
						new SignatureReader(field.signature).accept(traceVis);
						typeDecl = traceVis.getDeclaration();
						if (typeDecl.startsWith(" extends ")) {
							// ??
							typeDecl = typeDecl.substring(" extends ".length());
						}
						
//						val dump = new DumpSignatureVisitor();
//						new SignatureReader(field.signature).accept(dump);
//						System.out.println(" " + dump.getDumpText());
						
						val typeArgs = typeArgsSig.getTypeArguments();
						val classTypeArgs = LsUtils.filter(typeArgs, x -> !x.startsWith("java/lang/") && !x.startsWith("java/util/") && !formalTypeParams.contains(x));
						if (! classTypeArgs.isEmpty()) {
//							System.out.println("  .. " + classTypeArgs);
						}
						
					}
					
					typeDecl = typeDecl.replace("java.lang.", "");
					typeDecl = typeDecl.replace("java.util.regex.", "");
					typeDecl = typeDecl.replace("java.util.", "");
					System.out.println("   " + typeDecl + "  " + field.name);
//					System.out.println("   " + " " + field.desc 
//								+ ((field.signature != null)? " sig:" + field.signature : "")
//								);
				}
			}
		}
	}

	
	private static class TypeArgCollectorSignatureVisitor extends SignatureVisitor {
		@Getter
		private List<String> formalTypeParams = new ArrayList<>();
		@Getter
		private List<String> typeArguments = new ArrayList<>();
		@Getter
		private List<String> typeVariables = new ArrayList<>();
		@Getter
		private List<String> innerTypeVariables = new ArrayList<>();
		
		private final SignatureVisitor innerClassTypeSignatureVisitor;

		public TypeArgCollectorSignatureVisitor() {
			this(Opcodes.ASM9); // default?
		}
		
		public TypeArgCollectorSignatureVisitor(int api) {
			super(api);
			this.innerClassTypeSignatureVisitor = new SignatureVisitor(api) {
				@Override
				public void visitClassType(String name) {
					typeArguments.add(name);
				}
				@Override
				public void visitTypeVariable(final String name) {
					innerTypeVariables.add(name);
				}
			};
		}
		
		public void visitFormalTypeParameter(final String name) {
			formalTypeParams.add(name);
		}

		@Override
		public SignatureVisitor visitTypeArgument(char wildcard) {
			if (wildcard == '=') {
				return innerClassTypeSignatureVisitor;
			}
			return super.visitTypeArgument(wildcard);
		}

		@Override
		public void visitTypeVariable(final String name) {
			typeVariables.add(name);
		}
	
	}
	
	
	public static class DumpSignatureVisitor extends SignatureVisitor {
	
		StringBuilder sb = new StringBuilder();
		
		public String getDumpText() {
			return sb.toString();
		}

		  public DumpSignatureVisitor() {
				this(Opcodes.ASM9);
			}

	  public DumpSignatureVisitor(int api) {
			super(api);
		}

	public void visitFormalTypeParameter(final String name) {
		sb.append("FormalTypeParameter '" + name + "' ");
	}

	  public SignatureVisitor visitClassBound() {
		  sb.append("ClassBound ");
	    return this;
	  }

	  public SignatureVisitor visitInterfaceBound() {
		  sb.append("InterfaceBound ");
	    return this;
	  }

	  public SignatureVisitor visitSuperclass() {
		  sb.append("SuperClass ");
	    return this;
	  }

	  public SignatureVisitor visitInterface() {
		  sb.append("Interface ");
	    return this;
	  }

	  public SignatureVisitor visitParameterType() {
		  sb.append("ParamType ");
	    return this;
	  }

	  public SignatureVisitor visitReturnType() {
		  sb.append("ReturnType ");
	    return this;
	  }

	  public SignatureVisitor visitExceptionType() {
		  sb.append("ExceptionType ");
	    return this;
	  }

	  public void visitBaseType(final char descriptor) {
		  sb.append("BaseType '" + descriptor + "'");
	  }

	  public void visitTypeVariable(final String name) {
		  sb.append("TypeVariable '" + name + "' ");
		  
	  }

	  public SignatureVisitor visitArrayType() {
		  sb.append("ArrayType ");
	    return this;
	  }

	  public void visitClassType(final String name) {
		  sb.append("ClassType '" + name + "' ");
		  
	  }

	  public void visitInnerClassType(final String name) {
		  sb.append("InnerClassType '" + name + "' ");
	  }

	  public void visitTypeArgument() {
		  sb.append("TypeArgument ");
	  }

	  public SignatureVisitor visitTypeArgument(final char wildcard) {
		  sb.append("TypeArgument '" + wildcard + "' ");
	    return this;
	  }

	  public void visitEnd() {
		  sb.append("End ");
	  }
	}
	  
}
