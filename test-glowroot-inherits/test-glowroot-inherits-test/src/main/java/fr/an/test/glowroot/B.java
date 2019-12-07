package fr.an.test.glowroot;

public class B extends A {

    public B() {
	super();
	System.out.println("B.<init>()  .. after super()");
    }

    public void open() {
	System.out.println("B.open()");
    }

    public void close() {
	System.out.println("B.close()");
    }

}
