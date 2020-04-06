package fr.an.test.glowroot;

public class Main {

    public static void main(String[] args) {
	System.out.println("a = new A(); a.open(); a.close()");
	A a = new A();
	a.open();
	a.close();
	
	System.out.println("b = new B(); b.open(); b.close()");
	B b = new B();
	b.open();
	b.close();
    }
}
