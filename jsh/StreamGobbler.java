package jsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

class StreamGobbler implements Runnable {
    private final InputStream in;
    private final PrintStream out;

    private StreamGobbler(InputStream in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    public static Thread pipeAsync(InputStream in, PrintStream out) {
        Thread t = new Thread(new StreamGobbler(in, out), "gobbler");
        t.setDaemon(true);
        t.start();
        return t;
    }

    @Override
    public void run() {
        byte[] buf = new byte[8192];
        try {
            int n;
            while ((n = in.read(buf)) != -1) {
                SynchronizedPrinter.write(out, buf, 0, n);
            }
        } catch (IOException ignored) {
        }
    }
}
