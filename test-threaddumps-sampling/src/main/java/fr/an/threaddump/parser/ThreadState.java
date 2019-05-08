package fr.an.threaddump.parser;


public class ThreadState {

	public final String abbreviation;
    public final String state;
    public final String[] methods;

    ThreadState(String abbreviation, String state, String... methods) {
        this.abbreviation = abbreviation;
        this.state = state;
        this.methods = methods;
    }

    private static final ThreadState[] THREAD_STATES = {
            // Actual thread states. Use 1 character names.
            new ThreadState("U", "unknown"),
            new ThreadState("R", "runnable"),
            new ThreadState("R", Thread.State.RUNNABLE.name()),
            new ThreadState("E", "waiting for monitor entry"),
            new ThreadState("E", Thread.State.BLOCKED.name()),
            new ThreadState("e", "waiting for lock entry"),
            new ThreadState("W", "in Object.wait()", "java.lang.Object.wait"), // native code for Object.wait() in Java 1.4
            new ThreadState("W", Thread.State.WAITING.name()),
            new ThreadState("w", "waiting on monitor"),
            new ThreadState("P", "parking", "sun.misc.Unsafe.park"),
            new ThreadState("P", "waiting on VM lock", "java.util.concurrent.locks.LockSupport.park", "java.util.concurrent.locks.LockSupport.parkNanos", "java.util.concurrent.locks.LockSupport.parkUntil"), // Zing VM, also non-Unsafe parking
            new ThreadState("S", "sleeping", "java.lang.Thread.sleep"), // going to sleeping or awakening in Java 1.5
            new ThreadState("S", Thread.State.TIMED_WAITING.name()),
            new ThreadState("s", "waiting on condition"), // sleeping in Java 1.4
            new ThreadState("d", "suspended"),
            new ThreadState("N", Thread.State.NEW.name()),
            new ThreadState("T", Thread.State.TERMINATED.name()),
            // Terminating IO methods which block as 'runnable'. Use additional states with 2+ character names.
            new ThreadState("FR", "File read", "java.io.FileInputStream.read", "java.io.FileInputStream.readBytes", "java.io.RandomAccessFile.read", "java.io.RandomAccessFile.readBytes"),
            new ThreadState("FW", "File write", "java.io.FileOutputStream.write", "java.io.FileOutputStream.writeBytes", "java.io.RandomAccessFile.write", "java.io.RandomAccessFile.writeBytes"),
            new ThreadState("SA", "Socket accept", "java.net.PlainSocketImpl.socketAccept"),
            new ThreadState("SC", "Socket connect", "java.net.PlainSocketImpl.socketConnect"),
            new ThreadState("SR", "Socket read", "java.net.SocketInputStream.socketRead", "java.net.SocketInputStream.socketRead0"),
            new ThreadState("SW", "Socket write", "java.net.SocketOutputStream.socketWrite", "java.net.SocketOutputStream.socketWrite0"),
            new ThreadState("NA", "NIO accept", "sun.nio.ch.ServerSocketChannelImpl.accept0"),
            new ThreadState("NS", "NIO select", "sun.nio.ch.PollArrayWrapper.poll0", "sun.nio.ch.DevPollArrayWrapper.poll0", "sun.nio.ch.EPollArrayWrapper.epollWait"),
            new ThreadState("DP", "Datagram peek", "java.net.PlainDatagramSocketImpl.peek", "java.net.PlainDatagramSocketImpl.peekData"),
            new ThreadState("DR", "Datagram receive", "java.net.PlainDatagramSocketImpl.receive", "java.net.PlainDatagramSocketImpl.receive0", "java.net.TwoStacksPlainDatagramSocketImpl.receive0", "java.net.DualStackPlainDatagramSocketImpl.socketReceiveOrPeekData"),
            new ThreadState("DS", "Datagram send", "java.net.PlainDatagramSocketImpl.send", "java.net.TwoStacksPlainDatagramSocketImpl.send", "java.net.DualStackPlainDatagramSocketImpl.socketSend"),
    };
    // private static final int WAITING_FOR_LOCK_ENTRY = findThreadState("waiting for lock entry", "", "");
    private static final String JAVA_LANG_THREAD_STATE = "java.lang.Thread.State:";

    public static int findThreadState(String threadLine, String stateLine, String method) {
        for (int threadState = 0; threadState < THREAD_STATES.length; threadState++)
            for (String m : THREAD_STATES[threadState].methods)
                if (method.equals(m))
                    return threadState;
        int k = threadLine.indexOf('"', 1);
        for (int threadState = 0; threadState < THREAD_STATES.length; threadState++)
            if (threadLine.indexOf(THREAD_STATES[threadState].state, k + 1) >= 0)
                return threadState;
        if (stateLine.length() != 0) {
            k = stateLine.indexOf(JAVA_LANG_THREAD_STATE) + JAVA_LANG_THREAD_STATE.length();
            for (int threadState = 0; threadState < THREAD_STATES.length; threadState++)
                if (stateLine.indexOf(THREAD_STATES[threadState].state, k + 1) >= 0)
                    return threadState;
        }
        return 0;
    }
}
