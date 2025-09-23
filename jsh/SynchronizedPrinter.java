package jsh;

import java.io.PrintStream;

class SynchronizedPrinter {
    private static final Object LOCK = new Object();

    static void write(PrintStream out, byte[] b, int off, int len) {
        synchronized (LOCK) {
            out.write(b, off, len);
            out.flush();
        }
    }
}
